package com.teleprompter.teleprompter.dtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.teleprompter.teleprompter.entity.City;
import com.teleprompter.teleprompter.entity.User;
import com.teleprompter.teleprompter.enums.JourneyStatus;
import com.teleprompter.teleprompter.enums.TransportMode;

import lombok.Getter;
import lombok.Setter;


@Getter   @Setter
public class JourneyResponse {

	private Long id;
	
	private User traveler;
	
	private City sourceCity;
	
	private City destinationCity;
	
	private LocalDateTime departureTime;
	
	private LocalDateTime estimatedArrivalTime;
	
	private BigDecimal maxWeightCapacity;
	
	private BigDecimal availableWeightCapacity;
	
	private TransportMode transportMode;
	
	private JourneyStatus status;
	
}
