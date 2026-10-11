package com.teleprompter.teleprompter.service;

import java.time.LocalDateTime;
import java.util.Objects;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

	@Transactional(readOnly = true) 
	public DeliveryRequestResponse getDeliveryRequestById(Long id) {   // we can use as a Location header at endpoint
		
		DeliveryRequest request = deliveryRequestRepo.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("DeliveryRequest","id",id));
		
		return deliveryMapper.toResponse(request);
	}
	
	@Transactional
	public DeliveryRequestResponse acceptRequest(Long id) {
		
		DeliveryRequest request = deliveryRequestRepo.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("DeliveryRequest","id",id));
		
		// TODO: Validate that 'user' is the owner (traveler) of the journey
		if(!request.getStatus().canTransitionTo(DeliveryRequestStatus.ACCEPTED)) {
			throw new BusinessRuleViolationException("Cannot accept a request that is not PENDING.");
		}
		

		Journey journey = request.getJourney();
		Parcel parcel = request.getParcel();
		
		// 422 Business	Re-checks
		if(journey.getDepartureTime().isBefore(LocalDateTime.now())) {
			throw new BusinessRuleViolationException("Cannot accept a request for a journey that has already departed.");
		}
		
		if(journey.getStatus() != JourneyStatus.PUBLISHED) {
			throw new BusinessRuleViolationException("Traveler is no longer actively published.");
		}
		
		if(parcel.getStatus() != ParcelStatus.AWAITING_MATCH) {
			throw new BusinessRuleViolationException("Parcel has already been matched with another traveler.");
		}
		
		if(journey.getAvailableWeightCapacity().compareTo(parcel.getWeight()) < 0) {
			throw new BusinessRuleViolationException("The parcel weight exceeds the remaining capacity of the journey.");
		}
		
		
		// Effects
		request.setStatus(DeliveryRequestStatus.ACCEPTED);
		request.setAcceptedAt(LocalDateTime.now());
		
		parcel.setStatus(ParcelStatus.MATCHED);
		journey.setAvailableWeightCapacity(journey.getAvailableWeightCapacity().subtract(parcel.getWeight()));
		
		 // Flush before mapping to trigger version checks early		
		deliveryRequestRepo.flush();
		
		log.info("Delivery request accepted with id: {}", request.getId());
		
		return deliveryMapper.toResponse(request);

	}
	
	@Transactional
	public DeliveryRequestResponse rejectRequest(Long id) {
		DeliveryRequest request = deliveryRequestRepo.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("DeliveryRequest","id",id));
		
		// TODO: Validate that 'user' is the owner (traveler) of the journey
		if(!request.getStatus().canTransitionTo(DeliveryRequestStatus.REJECTED)) {
			throw new BusinessRuleViolationException("Can only reject a request that is currently PENDING.");
		}
		
		// State Transition
		request.setStatus(DeliveryRequestStatus.REJECTED);
		request.setClosedAt(LocalDateTime.now());
		
		deliveryRequestRepo.flush();   // Flush before mapping to trigger version checks early	
		
		log.info("Delivery request rejected with id: {}", request.getId());
		
		return deliveryMapper.toResponse(request);
	
	}
	
	@Transactional
	public DeliveryRequestResponse cancelRequest(Long id) {
		
		DeliveryRequest request = deliveryRequestRepo.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("DeliveryRequest","id",id));
		
		// TODO: Validate that 'user' is the owner (sender) of the parcel
		DeliveryRequestStatus oldStatus = request.getStatus();
		if(!oldStatus.canTransitionTo(DeliveryRequestStatus.CANCELLED)) {
			throw new BusinessRuleViolationException("Cannot cancel a request that is already closed.");
		}
		
//		State Transition   (Effects)
		request.setStatus(DeliveryRequestStatus.CANCELLED);
		request.setClosedAt(LocalDateTime.now());
		
//		Special Business Rule: If the request was already accepted, restore the held capacity.
		if(oldStatus == DeliveryRequestStatus.ACCEPTED) {
			Journey journey = request.getJourney();
			Parcel parcel = request.getParcel();
			

			journey.setAvailableWeightCapacity(journey.getAvailableWeightCapacity().add(parcel.getWeight()));
			

			parcel.setStatus(ParcelStatus.AWAITING_MATCH);
		
		}
		
		deliveryRequestRepo.flush();    // Flush before mapping to trigger version checks early	
		
		log.info("Delivery request canceled with id: {}", request.getId());
		
		return deliveryMapper.toResponse(request);
		
	}
	
	@Transactional(readOnly = true)
	public Page<DeliveryRequestResponse> getListByJourney(Long journeyId,Pageable pageable){
		
		// 1. First, check whether the journey actually exists; if not, return a 404.
		if(!journeyRepo.existsById(journeyId)) {
			throw new ResourceNotFoundException("Journey","id",journeyId);
		}
		
		// 2. Load the page using a single query from the database (without N+1)
		Page<DeliveryRequest> requestPage = deliveryRequestRepo.findByJourneyId(journeyId, pageable);
		
		// 3. Map while maintaining a stable page shape
		return requestPage.map(deliveryMapper::toResponse);
	
	}
	
	@Transactional
	public DeliveryRequestResponse pickupRequest(Long id) {
		DeliveryRequest request = deliveryRequestRepo.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("DeliveryRequest","id",id));
		
		 // TODO: Owner check (Journey's traveler)
		
//		Rule: Journey must be IN_PROGRESS to pick up
		if(request.getJourney().getStatus() != JourneyStatus.IN_PROGRESS) {
			throw new BusinessRuleViolationException("Cannot pick up a parcel unless the journey is IN_PROGRESS.");
		}
		
//		State Machine Transition Check(Accepted -> Pick up)
		if(!request.getStatus().canTransitionTo(DeliveryRequestStatus.PICKED_UP)) {
			throw new BusinessRuleViolationException("Invalid status transition for pickup.");
		}
		
		// Effects
		request.setStatus(DeliveryRequestStatus.PICKED_UP);
		request.setPickedUpAt(LocalDateTime.now());
		
		
		// Parcel State Change
		request.getParcel().setStatus(ParcelStatus.IN_TRANSIT);
		
		// Enforce optimistic lock checking before mapping
		deliveryRequestRepo.flush();  
		
		return deliveryMapper.toResponse(request);
	}
	
}

