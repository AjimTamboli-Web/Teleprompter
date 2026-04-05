package com.teleprompter.teleprompter.dtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.teleprompter.teleprompter.enums.ParcelCategory;

import lombok.Getter;
import lombok.Setter;

@Setter 
@Getter
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
	
	
}
