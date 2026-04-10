package com.teleprompter.teleprompter.dtos;

import java.math.BigDecimal;

import com.teleprompter.teleprompter.enums.TransportMode;

import lombok.Getter;
import lombok.Setter;

@Getter  @Setter
public class CreateJourneyRequest {

	
	private Long travelerId;
	
	private Long sourceCityId;
	
	private Long destinationCityId;
	
	private BigDecimal maxWeightCapacity;
	
	private TransportMode transportMode;
	
	
	
	
}
