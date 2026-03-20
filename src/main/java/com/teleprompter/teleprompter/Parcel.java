package com.teleprompter.teleprompter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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

@Entity @Getter @Setter
public class Parcel {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "sender_id",nullable = false)
	private User senderId;
	
	@Column(columnDefinition = "TEXT")
	private String description;
	
	@Column(precision = 6,scale = 2,nullable = false)
	private BigDecimal weight;
	
	@Column(precision = 6,scale = 2,nullable = false)
	private BigDecimal value;
	
	@Column(nullable = false)
	private boolean fragile;
	
	@Column(length = 50,nullable = false)
	private String photoUrl;
	
	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private ParcelCategory category;
	
	@Column(nullable = false)
	private boolean restrictedItems;
	
	@Column(nullable = false)
	private LocalDateTime createdAt;
	
	@Column(nullable = false)
	private LocalDateTime updatedAt;
	
	
	
}
