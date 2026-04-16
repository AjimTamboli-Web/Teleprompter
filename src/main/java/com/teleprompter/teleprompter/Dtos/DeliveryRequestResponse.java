package com.teleprompter.teleprompter.dtos;


import java.time.LocalDateTime;

import com.teleprompter.teleprompter.enums.DeliveryRequestStatus;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class DeliveryRequestResponse {

	
	private Long id;
	
	private String pickupAddress;
	private String dropAddress;
	
	private DeliveryRequestStatus status;
	
	 // Lifecycle Milestone Timestamps
	private LocalDateTime acceptedAt;
	private LocalDateTime pickedUpAt;
	private LocalDateTime deliveredAt;
	private LocalDateTime closedAt;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	 
	 // Fixed Nested Fields: Using custom screen-specific summaries instead of raw entities
	private ParcelSummaryForRequestResponse parcel;
	private JourneySummaryResponseForDelivery journey;
	
}
