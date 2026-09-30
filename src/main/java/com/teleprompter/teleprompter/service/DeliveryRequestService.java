package com.teleprompter.teleprompter.service;

import java.time.LocalDateTime;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.teleprompter.teleprompter.dtos.CreateDeliveryRequest;
import com.teleprompter.teleprompter.dtos.DeliveryRequestMapper;
import com.teleprompter.teleprompter.dtos.DeliveryRequestResponse;
import com.teleprompter.teleprompter.entity.DeliveryRequest;
import com.teleprompter.teleprompter.entity.Journey;
import com.teleprompter.teleprompter.entity.Parcel;
import com.teleprompter.teleprompter.enums.DeliveryRequestStatus;
import com.teleprompter.teleprompter.enums.JourneyStatus;
import com.teleprompter.teleprompter.enums.ParcelStatus;
import com.teleprompter.teleprompter.exception.BusinessRuleViolationException;
import com.teleprompter.teleprompter.exception.DuplicateResourceException;
import com.teleprompter.teleprompter.exception.ResourceNotFoundException;
import com.teleprompter.teleprompter.repository.DeliveryRequestRepository;
import com.teleprompter.teleprompter.repository.JourneyRepository;
import com.teleprompter.teleprompter.repository.ParcelRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DeliveryRequestService {

//	private static final Logger log = LoggerFactory.getLogger(DeliveryRequestService.class);
	
	private final DeliveryRequestRepository deliveryRequestRepo;
	private final ParcelRepository parcelRepo;
	private final JourneyRepository journeyRepo;
	private final DeliveryRequestMapper deliveryMapper;
	
	public DeliveryRequestService(DeliveryRequestRepository deliveryRequestRepo, ParcelRepository parcelRepo,
			JourneyRepository journeyRepo, DeliveryRequestMapper mapper) {
		this.deliveryRequestRepo = deliveryRequestRepo;
		this.parcelRepo = parcelRepo;
		this.journeyRepo = journeyRepo;
		this.deliveryMapper = mapper;
	}

	@Transactional
	public DeliveryRequestResponse createDeliveryRequest(CreateDeliveryRequest request) {

		// Rule 1: Parcel Validation
		Parcel parcel = parcelRepo.findById(request.getParcelId())
				.orElseThrow(() -> new ResourceNotFoundException("Parcel", "id", request.getParcelId()));
		if (parcel.getStatus() != ParcelStatus.AWAITING_MATCH) {
			throw new BusinessRuleViolationException("Parcel must be in AWAITING_MATCH status.");
		}

		// Rule 2: Journey Validation
		Journey journey = journeyRepo.findById(request.getJourneyId())
				.orElseThrow(() -> new ResourceNotFoundException("Journey", "id", request.getJourneyId()));
		if (journey.getStatus() != JourneyStatus.PUBLISHED) {
			throw new BusinessRuleViolationException("Journey must be in PUBLISHED status.");
		}

		// Rule 3:Past Validation
		if(journey.getDepartureTime().isBefore(LocalDateTime.now())) {
			throw new BusinessRuleViolationException("Cannot request a journey that has already departed.");
		}
		
		// Rule 4: Route match validation (Symmetrical validation using refactored fields!)
		if(!Objects.equals(parcel.getSourceCity().getId(), journey.getSourceCity().getId()) ||
			!Objects.equals(parcel.getDestinationCity().getId(), journey.getDestinationCity().getId())) {
			throw new BusinessRuleViolationException("Parcel route does not match journey route.");
		}
		
		// Rule 5: Weight capacity filter
		if(parcel.getWeight().compareTo(journey.getAvailableWeightCapacity()) > 0) {
			throw new BusinessRuleViolationException("Parcel weight exceeds the journey's available capacity.");
		}
		
		// Rule 6: Self-request block
		// TODO: Owner check (validate that authenticated user actually owns this parcel) comes with JWT
		if(Objects.equals(parcel.getSender().getId(), journey.getTraveler().getId())) {
			throw new BusinessRuleViolationException("Sender cannot request their own journey.");
		}
		
		// Rule 7: Duplicate pre-check
		if(deliveryRequestRepo.existsByParcelIdAndJourneyId(parcel.getId(), journey.getId())) {
			throw new DuplicateResourceException("Delivery","parcelId & journeyId", parcel.getId() + ":" + journey.getId());
		}
		
		// Mapping fields and copying addresses (since they are @NotBlank in DTO)
		DeliveryRequest deliveryRequest = new DeliveryRequest();
		
		deliveryRequest.setParcel(parcel);
		deliveryRequest.setJourney(journey);
		deliveryRequest.setPickupAddress(request.getPickupAddress());
		deliveryRequest.setDropAddress(request.getDropAddress());
		
//		Setting status explicitly for clear lifecycle visibility
		deliveryRequest.setStatus(DeliveryRequestStatus.PENDING);
		
		DeliveryRequest savedRequest = deliveryRequestRepo.saveAndFlush(deliveryRequest);
		
		// Added a log line at INFO level, with the request id only.
		log.info("Delivery request created with id: {}", savedRequest.getId());
		
		return deliveryMapper.toResponse(savedRequest);
	}

	@Transactional(readOnly = true) // skip dirty checking 
	public DeliveryRequestResponse getDeliveryRequestById(Long id) {   // we can use as a Location header at endpoint
		
		DeliveryRequest request = deliveryRequestRepo.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("DeliveryRequest","id",id));
		
		return deliveryMapper.toResponse(request);
	}
	
	@Transactional
	public DeliveryRequestResponse acceptRequest(Long id) {
		
		DeliveryRequest request = deliveryRequestRepo.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Delivery request","id",id));
		
		if(!request.getStatus().transitionTo(DeliveryRequestStatus.ACCEPTED)) {
			throw new BusinessRuleViolationException("Cannot accept a request that is not PENDING.");
		}
		
//		TODO : until JWT 
		Journey traveler = request.getJourney();
		Parcel parcel = request.getParcel();
		
		// 422 Business	Re-checks
		if(traveler.getStatus() != JourneyStatus.PUBLISHED) {
			throw new BusinessRuleViolationException("Traveler is no longer actively published.");
		}
		
		if(parcel.getStatus() != ParcelStatus.AWAITING_MATCH) {
			throw new BusinessRuleViolationException("Parcel has already been matched with another traveler.");
		}
		
		if(traveler.getAvailableWeightCapacity().compareTo(parcel.getWeight()) < 0) {
			throw new BusinessRuleViolationException("The parcel weight exceeds the remaining capacity of the journey.");
		}
		
		
		// Effects
		request.setStatus(DeliveryRequestStatus.ACCEPTED);
		request.setAcceptedAt(LocalDateTime.now());
		
		parcel.setStatus(ParcelStatus.MATCHED);
		traveler.setAvailableWeightCapacity(traveler.getAvailableWeightCapacity().subtract(parcel.getWeight()));
		
		
//		Our choice: Auto-cancel the remaining PENDING requests
//		(You can run custom queries or loops in the repository; here, we are keeping the database clean.)
		return deliveryMapper.toResponse(request);

	}
	
	@Transactional
	public DeliveryRequestResponse rejectRequest(Long id) {
		DeliveryRequest request = deliveryRequestRepo.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("DeliveryRequest","id",id));
		
//		State Machine check: Is it possible to move from PENDING to REJECTED?
		if(!request.getStatus().transitionTo(DeliveryRequestStatus.REJECTED)) {
			throw new BusinessRuleViolationException("Can only reject a request that is currently PENDING.");
		}
		
		// State Transition
		request.setStatus(DeliveryRequestStatus.REJECTED);
		request.setClosedAt(LocalDateTime.now());
		
		// Dirty checking automatically fires the UPDATE query at the end of transaction
		return deliveryMapper.toResponse(request);
	
	}
}

