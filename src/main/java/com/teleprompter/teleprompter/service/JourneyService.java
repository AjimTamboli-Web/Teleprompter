package com.teleprompter.teleprompter.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.teleprompter.teleprompter.dtos.CreateJourneyRequest;
import com.teleprompter.teleprompter.dtos.JourneyResponse;
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
		
		User traveler = userRepository.findById(request.getTravelerId())
				.orElseThrow(() -> new ResourceNotFoundException("User","id",request.getTravelerId()));
		
		if(traveler.getStatus() != UserStatus.ACTIVE) {
			throw new BusinessRuleViolationException("Traveler account is not active, Current status: " + traveler.getStatus());
		}
		
		return null;
	}
	
}



