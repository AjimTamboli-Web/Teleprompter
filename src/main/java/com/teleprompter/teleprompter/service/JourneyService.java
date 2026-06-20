package com.teleprompter.teleprompter.service;

import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.teleprompter.teleprompter.dtos.CreateJourneyRequest;
import com.teleprompter.teleprompter.dtos.JourneyResponse;
import com.teleprompter.teleprompter.entity.City;
import com.teleprompter.teleprompter.entity.User;
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

		if (Objects.equals(request.getSourceCityId(), request.getDestinationCityId())) {
			throw new BusinessRuleViolationException("Source and destination cities must be different.");
		}

		if (!request.getEstimatedArrivalTime().isAfter(request.getDepartureTime())) {
			throw new BusinessRuleViolationException("Arrival time must be strictly after the departure time.");
		}

		// [Database operations]
		User traveler = userRepository.findById(request.getTravelerId())
				.orElseThrow(() -> new ResourceNotFoundException("User", "id", request.getTravelerId()));

		if (traveler.getStatus() != UserStatus.ACTIVE) {
			throw new BusinessRuleViolationException("Traveler account is not eligible to publish journeys.");
		}

		City sourceCity = cityRepository.findById(request.getSourceCityId())
				.orElseThrow(() -> new ResourceNotFoundException("City", "id", request.getSourceCityId()));

		City destinationCity = cityRepository.findById(request.getDestinationCityId())
				.orElseThrow(() -> new ResourceNotFoundException("City", "id", request.getDestinationCityId()));

		return null;
	}

}
