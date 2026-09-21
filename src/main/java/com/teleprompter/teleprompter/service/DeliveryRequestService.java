package com.teleprompter.teleprompter.service;

import java.time.LocalDateTime;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.teleprompter.teleprompter.dtos.CreateDeliveryRequest;
import com.teleprompter.teleprompter.dtos.DeliveryRequestMapper;
import com.teleprompter.teleprompter.dtos.DeliveryRequestResponse;
import com.teleprompter.teleprompter.entity.DeliveryRequest;
import com.teleprompter.teleprompter.entity.Journey;
import com.teleprompter.teleprompter.entity.Parcel;
import com.teleprompter.teleprompter.enums.JourneyStatus;
import com.teleprompter.teleprompter.enums.ParcelStatus;
import com.teleprompter.teleprompter.exception.BusinessRuleViolationException;
import com.teleprompter.teleprompter.exception.DuplicateResourceException;
import com.teleprompter.teleprompter.exception.ResourceNotFoundException;
import com.teleprompter.teleprompter.repository.DeliveryRequestRepository;
import com.teleprompter.teleprompter.repository.JourneyRepository;
import com.teleprompter.teleprompter.repository.ParcelRepository;

@Service
public class DeliveryRequestService {

	private static final Logger log = LoggerFactory.getLogger(DeliveryRequestService.class);
	
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
		if(deliveryRequestRepo.existsByParcelIdAndJourneyId(request.getParcelId(), request.getJourneyId())) {
			throw new DuplicateResourceException("Delivery","parcelId & journeyId", request.getParcelId() + " " + request.getJourneyId());
		}
		
		// Mapping fields and copying addresses (since they are @NotBlank in DTO)
		DeliveryRequest deliveryRequest = new DeliveryRequest();
		
		deliveryRequest.setParcel(parcel);
		deliveryRequest.setJourney(journey);
		deliveryRequest.setPickupAddress(request.getPickupAddress());
		deliveryRequest.setDropAddress(request.getDropAddress());
		
		DeliveryRequest savedRequest = deliveryRequestRepo.save(deliveryRequest);
		
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
	
}
