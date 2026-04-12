package com.teleprompter.teleprompter.dtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.teleprompter.teleprompter.enums.TransportMode;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter  @Setter
public class CreateJourneyRequest {

	@NotNull
	private UUID travelerId;
	
	@NotNull
	private Long sourceCityId;
	
	@NotNull
	private Long destinationCityId;
	
	@NotNull
	private LocalDateTime departureTime;
	
	@NotNull
	private LocalDateTime estimatedArrivalTime;
	
	@NotNull
	@DecimalMin(value = "0.1")
	private BigDecimal maxWeightCapacity;
	
	@NotNull
	private TransportMode transportMode;
	
	
	
	
}
