package com.teleprompter.teleprompter.dtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.teleprompter.teleprompter.enums.ParcelCategory;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ParcelResponse {

	public ParcelResponse() {
		
	}
	
	private Long id;
	private SenderSummaryResponse sender;
	private String description;
	private BigDecimal weight;
	private BigDecimal value;
	private boolean fragile;
	private boolean restrictedItemsDeclared;
	private String photoUrl;
	private ParcelCategory category;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	
	public ParcelResponse(Long id, SenderSummaryResponse sender, String description, BigDecimal weight, 
			BigDecimal value, boolean fragile, boolean restricatedItemsDeclare, String photoUrl, 
			ParcelCategory category, LocalDateTime createdAt, LocalDateTime updatedAt
			) {
		this.id = id;
		this.sender = sender;
		this.description = description;
		this.weight = weight;
		this.value = value;
		this.fragile = fragile;
		this.restrictedItemsDeclared = restricatedItemsDeclare;
		this.photoUrl = photoUrl;
		this.category = category;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}
	
	
}
