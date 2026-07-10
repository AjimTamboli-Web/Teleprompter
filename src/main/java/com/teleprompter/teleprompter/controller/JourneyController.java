package com.teleprompter.teleprompter.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.teleprompter.teleprompter.dtos.CreateJourneyRequest;
import com.teleprompter.teleprompter.dtos.JourneyResponse;
import com.teleprompter.teleprompter.service.JourneyService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/journeys")
public class JourneyController {

	private final JourneyService journeyService;
	
	public JourneyController(JourneyService journeyService) {
		this.journeyService = journeyService;
	}
	
	@PostMapping
	public ResponseEntity<JourneyResponse> publishJourney(@Valid @RequestBody CreateJourneyRequest request){
		
		JourneyResponse responseBody = journeyService.publishJourney(request);
		
		 // TODO: Add Location header once GET /api/journeys/{id} is implemented.
        // We cannot supply a broken or non-existent URI in the response headers for now.
		return ResponseEntity
				.status(HttpStatus.CREATED)
				.body(responseBody);
	}
	
	
}
