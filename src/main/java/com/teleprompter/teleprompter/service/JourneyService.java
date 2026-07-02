package com.teleprompter.teleprompter.service;

import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.teleprompter.teleprompter.dtos.CreateJourneyRequest;
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

@Service
public class JourneyService {

	private final JourneyRepository journeyRepository;
	private final UserRepository userRepository;
	private final CityRepository cityRepository;

	public JourneyService(JourneyRepository journeyRepo, UserRepository userRepo, CityRepository cityRepo) {
		this.journeyRepository = journeyRepo;
		this.userRepository = userRepo;
		this.cityRepository = cityRepo;
	}

	@Transactional
	public JourneyResponse publishJourney(CreateJourneyRequest request) {

		// [FAIL FAST]: Memory checks
		if (Objects.equals(request.getSourceCityId(), request.getDestinationCityId())) {
			throw new BusinessRuleViolationException("Source and destination cities must be different.");
		}

		if (!request.getEstimatedArrivalTime().isAfter(request.getDepartureTime())) {
			throw new BusinessRuleViolationException("Arrival time must be strictly after the departure time.");
		}

		// [Database Operations]: Heavy and expensive checks will begin after this.
		
		//Load user via travelerId
		User traveler = userRepository.findById(request.getTravelerId())
				.orElseThrow(() -> new ResourceNotFoundException("User", "id", request.getTravelerId()));

		// Check the passenger's ACTIVE status.
		if (traveler.getStatus() != UserStatus.ACTIVE) {
			throw new BusinessRuleViolationException("Traveler account is not eligible to publish journeys.");
		}

		// Load both cities
		City sourceCity = cityRepository.findById(request.getSourceCityId())
				.orElseThrow(() -> new ResourceNotFoundException("City", "id", request.getSourceCityId()));

		City destinationCity = cityRepository.findById(request.getDestinationCityId())
				.orElseThrow(() -> new ResourceNotFoundException("City", "id", request.getDestinationCityId()));

		// Creation of the Journey object (explicit assignment at the service level)
		Journey journey = new Journey();
		
		// Fields coming from the DTO
		journey.setDepartureTime(request.getDepartureTime());
		journey.setEstimatedArrivalTime(request.getEstimatedArrivalTime());
		journey.setMaxWeightCapacity(request.getMaxWeightCapacity());
		
		// Actual loaded entities (object references)
		journey.setTraveler(traveler);
		journey.setSourceCity(sourceCity);
		journey.setDestinationCity(destinationCity);
		
		// [FIX]: Set transportMode to prevent 500 DB Exception.
		journey.setTransportMode(request.getTransportMode());
		
		// Server-determined fields (explicit rules of the service layer)
		journey.setStatus(JourneyStatus.PUBLISHED);
		journey.setAvailableWeightCapacity(request.getMaxWeightCapacity());
		
		
		return null;
	}

}
