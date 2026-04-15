package com.teleprompter.teleprompter.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter  @Setter
public class CreateDeliveryRequest {
 
	@NotNull
	private Long parcelId;
	
	@NotNull
	private Long journeyId;
	
	@NotBlank
	@Size(max = 255)
	private String pickupAddress;
	
	@NotBlank
	@Size(max = 255)
	private String dropAddress;
	
	
	
	
}
