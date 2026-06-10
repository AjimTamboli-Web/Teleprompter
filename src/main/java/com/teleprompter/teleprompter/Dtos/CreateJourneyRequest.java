package com.teleprompter.teleprompter.dtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.teleprompter.teleprompter.enums.TransportMode;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter  @Setter
public class CreateJourneyRequest {

	// TODO: Temporary scaffolding. Remove once JWT authentication is implemented.
	// The travelerId must come from the authenticated principal to prevent IDOR / Broken Access Control vulnerabilities.
	@NotNull
	private UUID travelerId;
	
	@NotNull
	private Long sourceCityId;
	
	@NotNull
	private Long destinationCityId;
	
	@NotNull
	@Future(message = "Departure time must be in the future") //it checks that a date/time is after the current moment
	private LocalDateTime departureTime; // it blocks "journeys in the past" at the entrance, before any service logic runs
	
	@NotNull
	private LocalDateTime estimatedArrivalTime;
	
	@NotNull
	@DecimalMin(value = "0.1")
	@DecimalMax(value = "9999.99")
	@Digits(integer = 4, fraction = 2)
	private BigDecimal maxWeightCapacity;
	
	@NotNull
	private TransportMode transportMode;
	
	
	
	
}
