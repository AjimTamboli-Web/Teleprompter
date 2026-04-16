package com.teleprompter.teleprompter.dtos;

import java.time.LocalDateTime;

import com.teleprompter.teleprompter.enums.TransportMode;

import lombok.Getter;
import lombok.Setter;


@Getter   @Setter
public class JourneySummaryResponseForDelivery {

	
	private Long id;
	
	private LocalDateTime departureTime;
	private TransportMode transportMode;

	// Reusing CitySummaryResponse legitimately based on shared purpose
	private CitySummaryResponse sourceCity;
	private CitySummaryResponse destinationCity;
	
	
	
	
}
