package com.teleprompter.teleprompter.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.teleprompter.teleprompter.enums.ParcelCategory;

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
import jakarta.persistence.SequenceGenerator;
import lombok.Getter;
import lombok.Setter;

@Entity @Getter @Setter
public class Parcel {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "parcel_seq")
	@SequenceGenerator(name = "parcel_seq",sequenceName = "parcel_sequence",allocationSize = 50)
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "sender_id",nullable = false)
	private User sender;
	
	@Column(columnDefinition = "TEXT")
	private String description;
	
	@Column(precision = 6,scale = 2,nullable = false)
	private BigDecimal weight;
	
	@Column(precision = 10,scale = 2,nullable = false)
	private BigDecimal value;
	
	@Column(nullable = false)
	private boolean fragile;
	
	@Column(nullable = false)
	private boolean restrictedItemsDeclared;
	
	@Column(length = 500,nullable = false)
	private String photoUrl;
	
	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private ParcelCategory category;
	
	@Column(nullable = false)
	@CreationTimestamp
	private LocalDateTime createdAt;
	
	@Column(nullable = false)
	@UpdateTimestamp
	private LocalDateTime updatedAt;
	
	
	
}
