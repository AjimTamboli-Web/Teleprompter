package com.teleprompter.teleprompter.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter  @Setter
public class CreateDeliveryRequest {

	private Long parcelId;
	
	private Long journeyId;
	
	private String pickupAddress;
	
	private String dropAddress;
	
	
	
	
}
