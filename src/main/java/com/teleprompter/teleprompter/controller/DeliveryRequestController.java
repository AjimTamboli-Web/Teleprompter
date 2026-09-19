package com.teleprompter.teleprompter.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.teleprompter.teleprompter.service.DeliveryRequestService;

@RestController
@RequestMapping("/api/delivery-requests")
public class DeliveryRequestController {

	private final DeliveryRequestService requestService;
	
	public DeliveryRequestController(DeliveryRequestService service) {
		this.requestService = service;
	}
	
	
	
	
	
}
