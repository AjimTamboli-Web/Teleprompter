package com.teleprompter.teleprompter.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.teleprompter.teleprompter.dtos.CreateJourneyRequest;
import com.teleprompter.teleprompter.dtos.JourneyMapper;
import com.teleprompter.teleprompter.dtos.JourneyResponse;
import com.teleprompter.teleprompter.entity.City;
import com.teleprompter.teleprompter.entity.DeliveryRequest;
import com.teleprompter.teleprompter.entity.Journey;
import com.teleprompter.teleprompter.entity.User;
import com.teleprompter.teleprompter.enums.DeliveryRequestStatus;
import com.teleprompter.teleprompter.enums.JourneyStatus;
import com.teleprompter.teleprompter.enums.ParcelStatus;
import com.teleprompter.teleprompter.enums.UserStatus;
import com.teleprompter.teleprompter.exception.BusinessRuleViolationException;
import com.teleprompter.teleprompter.exception.ResourceNotFoundException;
import com.teleprompter.teleprompter.repository.CityRepository;
import com.teleprompter.teleprompter.repository.DeliveryRequestRepository;
import com.teleprompter.teleprompter.repository.JourneyRepository;
import com.teleprompter.teleprompter.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j // Lombok annotation that creates a 'log' field in the background
public class JourneyService {

	private final JourneyRepository journeyRepository;
	private final UserRepository userRepository;
	private final CityRepository cityRepository;
	private final JourneyMapper journeyMapper;
	private final DeliveryRequestRepository deliveryRequestRepository;

	public JourneyService(JourneyRepository journeyRepo, UserRepository userRepo, CityRepository cityRepo,
			JourneyMapper journeyMapper, DeliveryRequestRepository deliveryRequestRepository) {
		this.journeyRepository = journeyRepo;
		this.userRepository = userRepo;
		this.cityRepository = cityRepo;
		this.journeyMapper = journeyMapper;
		this.deliveryRequestRepository = deliveryRequestRepository;
	}

	@Transactional
	public JourneyResponse publishJourney(CreateJourneyRequest request) {

		if (Objects.equals(request.getSourceCityId(), request.getDestinationCityId())) {
			throw new BusinessRuleViolationException("Source and destination cities must be different.");
		}

		if (!request.getEstimatedArrivalTime().isAfter(request.getDepartureTime())) {
			throw new BusinessRuleViolationException("Arrival time must be strictly after the departure time.");
		}

		User traveler = userRepository.findById(request.getTravelerId())
				.orElseThrow(() -> new ResourceNotFoundException("User", "id", request.getTravelerId()));

		if (traveler.getStatus() != UserStatus.ACTIVE) {
			throw new BusinessRuleViolationException("Traveler account is not eligible to publish journeys.");
		}

		City sourceCity = cityRepository.findById(request.getSourceCityId())
				.orElseThrow(() -> new ResourceNotFoundException("City", "id", request.getSourceCityId()));

		City destinationCity = cityRepository.findById(request.getDestinationCityId())
				.orElseThrow(() -> new ResourceNotFoundException("City", "id", request.getDestinationCityId()));

		Journey journey = new Journey();

		journey.setDepartureTime(request.getDepartureTime());
		journey.setEstimatedArrivalTime(request.getEstimatedArrivalTime());
		journey.setMaxWeightCapacity(request.getMaxWeightCapacity());

		journey.setTraveler(traveler);
		journey.setSourceCity(sourceCity);
		journey.setDestinationCity(destinationCity);

		journey.setTransportMode(request.getTransportMode());

		journey.setStatus(JourneyStatus.PUBLISHED);
		journey.setAvailableWeightCapacity(request.getMaxWeightCapacity());

		// Inserting records into the database (INSERT) and response mapping
		Journey saved = journeyRepository.save(journey);

//		 Safe parameterized INFO log line
		log.info("Journey successfully published with ID: {}", saved.getId());

		// Sending back a clean response without leaking the entity.
		return journeyMapper.toResponse(saved);
	}

	/**
	 * Fetches a single journey by its ID and safely maps it to a DTO while the
	 * Hibernate session is open to prevent lazy loading failures.
	 */
	@Transactional(readOnly = true) // Fix: readOnly = true for read optimization
	public JourneyResponse getJourneyById(Long id) {

		Journey journey = journeyRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Journey", "id", id));

//		Returning a DTO by performing secure mapping within the transaction itself
		return journeyMapper.toResponse(journey);
	}

	/**
	 * Searches for active, published journeys matching the route and date range.
	 * Translates a single LocalDate into a clean, half-open LocalDateTime 24-hour
	 * interval.
	 */
	@Transactional(readOnly = true)
	public Page<JourneyResponse> searchJourneys(Long sourceCityId, Long destinationCityId, LocalDate date,
			Pageable pageable) {

//		Translation of a date into a half-open 24-hour interval
		LocalDateTime fromTime = date.atStartOfDay(); // Today at 00:00
		LocalDateTime toTime = date.plusDays(1).atStartOfDay(); // Tomorrow morning 00:00

		Page<Journey> journeyPage = journeyRepository.searchJourney(sourceCityId, destinationCityId, fromTime, toTime,
				JourneyStatus.PUBLISHED, pageable);

//	    Converting entities to DTOs while preserving pagination metadata using lambda 
		return journeyPage.map(journey -> journeyMapper.toResponse(journey)); // here you can also use method reference

	}

	@Transactional
	public void cancelJourney(Long id) {

		Journey journey = journeyRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Journey", "id", id));

		/*
		 * TODO : Owner check (only the journey's traveler may cancel) comes with JWT
		 */

//		ENUM state machine called
		if (!journey.getStatus().canTransitionTo(JourneyStatus.CANCELLED)) {
			throw new BusinessRuleViolationException("journey cannot be cancelled.");
		}

//		Only setter called, No repository.save() needed
		journey.setStatus(JourneyStatus.CANCELLED);

// 		🚀 Bulk Cascade Update using Memory Loops (Preserves Dirty Checking & @Version)
		List<DeliveryRequest> requests = deliveryRequestRepository.findByJourneyId(id);
		for(DeliveryRequest req : requests) {
			if(req.getStatus() == DeliveryRequestStatus.PENDING) {
				req.setStatus(DeliveryRequestStatus.CANCELLED);
				req.setClosedAt(LocalDateTime.now());
			} else if(req.getStatus() == DeliveryRequestStatus.ACCEPTED) {
				req.setStatus(DeliveryRequestStatus.CANCELLED);
				req.setClosedAt(LocalDateTime.now());
				req.getParcel().setStatus(ParcelStatus.AWAITING_MATCH); // Parcel back to market
			}
		}
	}

	@Transactional
	public JourneyResponse startJourney(Long id) {
		Journey journey = journeyRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Journey", "id", id));

		// TODO: Owner Check
		if (!journey.getStatus().canTransitionTo(JourneyStatus.IN_PROGRESS)) {
			throw new BusinessRuleViolationException("Invalid status transition.");
		}

		journey.setStatus(JourneyStatus.IN_PROGRESS);

		return journeyMapper.toResponse(journey);
	}

	@Transactional
	public JourneyResponse completeJourney(Long id) {
		Journey journey = journeyRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Journey", "id", id));

		// TODO: Owner Check

		// Check if any request is still ACCEPTED, PICKED_UP or IN_TRANSIT using our 'In' query.
		boolean hasActiveRequest = deliveryRequestRepository.existsByJourneyIdAndStatusIn(id,
				List.of(DeliveryRequestStatus.ACCEPTED,DeliveryRequestStatus.PICKED_UP,DeliveryRequestStatus.IN_TRANSIT));

		if(hasActiveRequest) {
			throw new BusinessRuleViolationException("Cannot complete journey while delivery requests are still active.");
		}
		
		if(!journey.getStatus().canTransitionTo(JourneyStatus.COMPLETED)) {
			throw new BusinessRuleViolationException("Invalid Status Transition.");
		}
		
		journey.setStatus(JourneyStatus.COMPLETED);
		
		return journeyMapper.toResponse(journey);
	}
}
