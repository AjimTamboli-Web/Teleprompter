package com.teleprompter.teleprompter.service;

import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.teleprompter.teleprompter.dtos.CreateJourneyRequest;
import com.teleprompter.teleprompter.dtos.JourneyMapper;
import com.teleprompter.teleprompter.dtos.JourneyResponse;
import com.teleprompter.teleprompter.entity.City;
import com.teleprompter.teleprompter.entity.Journey;
import com.teleprompter.teleprompter.entity.User;
import com.teleprompter.teleprompter.enums.JourneyStatus;
import com.teleprompter.teleprompter.enums.UserStatus;
import com.teleprompter.teleprompter.exception.BusinessRuleViolationException;
import com.teleprompter.teleprompter.exception.ResourceNotFoundException;
import com.teleprompter.teleprompter.repository.CityRepository;
import com.teleprompter.teleprompter.repository.JourneyRepository;
import com.teleprompter.teleprompter.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j   // Lombok annotation that creates a 'log' field in the background
public class JourneyService {

	private final JourneyRepository journeyRepository;
	private final UserRepository userRepository;
	private final CityRepository cityRepository;
	private final JourneyMapper journeyMapper;

	public JourneyService(JourneyRepository journeyRepo, UserRepository userRepo, CityRepository cityRepo,
			JourneyMapper journeyMapper) {
		this.journeyRepository = journeyRepo;
		this.userRepository = userRepo;
		this.cityRepository = cityRepo;
		this.journeyMapper = journeyMapper;
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
     * Fetches a single journey by its ID and safely maps it to a DTO 
     * while the Hibernate session is open to prevent lazy loading failures.
     */
	@Transactional(readOnly = true)  // Fix: readOnly = true for read optimization
	public JourneyResponse getJourneyById(Long id) {
		
		Journey journey = journeyRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Journey","id", id));
			
//		Returning a DTO by performing secure mapping within the transaction itself
		return journeyMapper.toResponse(journey);
	}
	
}
