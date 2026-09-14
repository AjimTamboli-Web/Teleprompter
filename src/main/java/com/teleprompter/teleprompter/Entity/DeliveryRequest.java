package com.teleprompter.teleprompter.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.teleprompter.teleprompter.enums.DeliveryRequestStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "delivery_request", uniqueConstraints = @UniqueConstraint(columnNames = { "parcel_id",
		"journey_id" }), indexes = @Index(name = "idx_delivery_request_journey", columnList = "journey_id"))
public class DeliveryRequest {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "delivery_seq")
	@SequenceGenerator(name = "delivery_seq", sequenceName = "delivery_sequence", allocationSize = 50)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "parcel_id", nullable = false)
	private Parcel parcel;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "journey_id", nullable = false)
	private Journey journey;

	@Column(length = 255, nullable = false)
	private String pickupAddress;

	@Column(length = 255, nullable = false)
	private String dropAddress;

	@Column(nullable = false)
	@ColumnDefault("'PENDING'")
	@Enumerated(EnumType.STRING)
	private DeliveryRequestStatus status = DeliveryRequestStatus.PENDING;

	private LocalDateTime acceptedAt;

	private LocalDateTime pickedUpAt;

	private LocalDateTime deliveredAt;

	private LocalDateTime closedAt;

	@CreationTimestamp
	@Column(nullable = false)
	private LocalDateTime createdAt;

	@UpdateTimestamp
	@Column(nullable = false)
	private LocalDateTime updatedAt;

}
