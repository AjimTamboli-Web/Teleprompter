package com.teleprompter.teleprompter;

import java.time.LocalDateTime;

import org.hibernate.annotations.ColumnDefault;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Entity   @Getter   @Setter
public class DeliveryRequest {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "parcel_id")
	private Parcel parcel;
	
	@ManyToOne(fetch = FetchType.LAZY) 
	@JoinColumn(name = "journey_id")
	private Journey journey;
	
	@Column(length = 255 )
	private String pickupAddress;
	@Column(length = 255)
	private String dropAddress;
	
	@ColumnDefault("PENDING")
	private String status = "PENDING";
	
	@Column(nullable = false)
	private LocalDateTime acceptedAt;
	
	@Column(nullable = false)
	private LocalDateTime pickedUpAt;
	
	@Column(nullable = false)
	private LocalDateTime deliveredAt;
	
	@Column(nullable = false)
	private LocalDateTime closedAt;
	
	@Column(nullable = false)
	private LocalDateTime createdAt;
	
	@Column(nullable = false)
	private LocalDateTime updatedAt;
	
	
	
	
	
}
