package com.teleprompter.teleprompter.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.teleprompter.teleprompter.dtos.CreateDeliveryRequest;
import com.teleprompter.teleprompter.dtos.DeliveryRequestResponse;
import com.teleprompter.teleprompter.service.DeliveryRequestService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/delivery-requests")
public class DeliveryRequestController {

	private final DeliveryRequestService requestService;
	
	public DeliveryRequestController(DeliveryRequestService service) {
		this.requestService = service;
	}
	
	@PostMapping
	public ResponseEntity<DeliveryRequestResponse> createRequest(@Valid @RequestBody CreateDeliveryRequest request){
		
		DeliveryRequestResponse response = requestService.createDeliveryRequest(request);
		
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
		
	}
	
	
	
	
}
