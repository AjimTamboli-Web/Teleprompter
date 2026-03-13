package com.teleprompter.teleprompter;

import java.math.BigDecimal;
import java.time.LocalDateTime;


import org.hibernate.annotations.ColumnDefault;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter   @Getter
public class Journey {

//	Journey — id BIGINT PK, traveler_id FK→User, source_city_id FK→City, 
//	destination_city_id FK→City, departure_time DATETIME, estimated_arrival_time DATETIME, 
//	max_weight_capacity DECIMAL(6,2), available_weight_capacity DECIMAL(6,2), 
//	transport_mode VARCHAR(20), status VARCHAR(20) DEFAULT 'PUBLISHED' 
//	(PUBLISHED/IN_PROGRESS/COMPLETED/CANCELLED)
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "traveler_id", nullable = false)
	private User traveler;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "source_city_id", nullable = false)
	private City sourceCity;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "destination_city_id", nullable = false)
	private City destinationCity;
	
	@Column(nullable = false)
	private LocalDateTime departureTime;
	
	@Column(nullable = false)
	private LocalDateTime estimatedArrivalTime;
	
	@Column(precision = 6, scale = 2, nullable = false)
	private BigDecimal maxWeightCapacity;
	
	@Column(precision = 6, scale = 2, nullable = false)
	private BigDecimal availableWeightCapacity;
	
	@Enumerated(EnumType.STRING)
	@Column(length = 20, nullable = false)
	private TransportMode transportMode;
	
	@ColumnDefault("'PUBLISHED'")
	@Column(length = 20, nullable = false)
	@Enumerated(EnumType.STRING)
	private JourneyStatus status = JourneyStatus.PUBLISHED;
}

