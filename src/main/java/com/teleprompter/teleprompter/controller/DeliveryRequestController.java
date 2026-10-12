package com.teleprompter.teleprompter.controller;

import java.net.URI;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.teleprompter.teleprompter.dtos.CreateDeliveryRequest;
import com.teleprompter.teleprompter.dtos.DeliveryRequestResponse;
import com.teleprompter.teleprompter.service.DeliveryRequestService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

@RestController
@RequestMapping("/api/delivery-requests")
public class DeliveryRequestController {

	// A constant cap has been set to protect the server from heavy load
		private static final int MAX_PAGE_SIZE = 50;
	
	private final DeliveryRequestService requestService;

	public DeliveryRequestController(DeliveryRequestService service) {
		this.requestService = service;
	}

	@PostMapping
	public ResponseEntity<DeliveryRequestResponse> createRequest(@Valid @RequestBody CreateDeliveryRequest request) {

		DeliveryRequestResponse responseBody = requestService.createDeliveryRequest(request);

		URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
				.buildAndExpand(responseBody.getId()).toUri();

		return ResponseEntity.created(location).body(responseBody);

	}

	@GetMapping("/{id}")
	public ResponseEntity<DeliveryRequestResponse> getRequestById(@PathVariable Long id) {

		DeliveryRequestResponse response = requestService.getDeliveryRequestById(id);

		return ResponseEntity.ok(response);
	}

	@PostMapping("/{id}/accept")
	public ResponseEntity<DeliveryRequestResponse> acceptRequest(@PathVariable Long id) {
		DeliveryRequestResponse response = requestService.acceptRequest(id);
		return ResponseEntity.ok(response);
	}

	@PostMapping("/{id}/reject")
	public ResponseEntity<DeliveryRequestResponse> rejectRequest(@PathVariable Long id) {
		DeliveryRequestResponse response = requestService.rejectRequest(id);
		return ResponseEntity.ok(response);
	}

	@PostMapping("/{id}/cancel")
	public ResponseEntity<DeliveryRequestResponse> cancelRequest(@PathVariable Long id) {
		DeliveryRequestResponse response = requestService.cancelRequest(id);
		return ResponseEntity.ok(response);
	}

	@GetMapping
	public ResponseEntity<Page<DeliveryRequestResponse>> getListByJourney(@RequestParam Long journeyId,
			@RequestParam(defaultValue = "0") @Min(0) int page,
			@RequestParam(defaultValue = "10") @Min(1) @Max(MAX_PAGE_SIZE) int size) {

//		 Stable Ordering Pattern: Sort by ID (or creation date) first, and then use an ID as a tie-breaker.
//		This ensures that the paging result remains completely stable and consistent (deterministic) every time.
		Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
		
		Page<DeliveryRequestResponse> responsePage = requestService.getListByJourney(journeyId, pageable);
		
		return ResponseEntity.ok(responsePage);
	}

	@PostMapping("/{id}/pickup")
	public ResponseEntity<DeliveryRequestResponse> pickupRequest(@PathVariable Long id){
		 // Current user parameter will be wired properly post-JWT task
		DeliveryRequestResponse response = requestService.pickupRequest(id);
		return ResponseEntity.ok(response);
	}
	
	
}
