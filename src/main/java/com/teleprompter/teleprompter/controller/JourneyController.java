package com.teleprompter.teleprompter.controller;

import java.net.URI;
import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.teleprompter.teleprompter.dtos.CreateJourneyRequest;
import com.teleprompter.teleprompter.dtos.JourneyResponse;
import com.teleprompter.teleprompter.service.JourneyService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

@RestController
@RequestMapping("/api/journeys")
public class JourneyController {


	// A constant cap has been set to protect the server from heavy load
	private static final int MAX_PAGE_SIZE = 50;
	
	private final JourneyService journeyService;

	public JourneyController(JourneyService journeyService) {
		this.journeyService = journeyService;
	}

	@PostMapping
	public ResponseEntity<JourneyResponse> publishJourney(@Valid @RequestBody CreateJourneyRequest request) {

		JourneyResponse responseBody = journeyService.publishJourney(request);

		URI location = ServletUriComponentsBuilder.fromCurrentRequest() // starts from the URL of this POST:
																		// /api/journeys
				.path("/{id}") // appends a template variable
				.buildAndExpand(responseBody.getId()) // fills {id} with the real value
				.toUri();

		// ResponseEntity.created(uri) sets status 201 and the Location header simultaneously.
		return ResponseEntity.created(location).body(responseBody);

	}

	@GetMapping("/{id}")
	public ResponseEntity<JourneyResponse> getJourneyById(@PathVariable Long id) {

		JourneyResponse responseBody = journeyService.getJourneyById(id);

		return ResponseEntity.ok(responseBody);
	}
	
	
	@GetMapping
	public ResponseEntity<Page<JourneyResponse>> searchJourneys(@RequestParam Long sourceCityId,
																@RequestParam Long destinationCityId,
																@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
																@RequestParam(defaultValue = "0") @Min(value = 0,message = "Page index must be greater than or equal to {value}.") int page,
																@RequestParam(defaultValue = "10") @Min(value = 1,message = "Page size must be at least {value}.") @Max(value = MAX_PAGE_SIZE, message = "Page size cannot exceed {value}.") int size){

		Pageable pageable = PageRequest.of(page, size);
		
		Page<JourneyResponse> results = journeyService.searchJourneys(sourceCityId, destinationCityId, date, pageable);
		
		return ResponseEntity.ok(results);
		
	}

	@PostMapping("/{id}/cancel")
	public ResponseEntity<JourneyResponse> cancelJourney(@PathVariable Long id){
		
		JourneyResponse updatedJourney = journeyService.cancelJourney(id);
		
		return ResponseEntity.ok(updatedJourney);
	}
}
