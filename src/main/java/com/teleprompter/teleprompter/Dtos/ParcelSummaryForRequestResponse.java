package com.teleprompter.teleprompter.dtos;

import java.math.BigDecimal;

import com.teleprompter.teleprompter.enums.ParcelCategory;

import lombok.Getter;
import lombok.Setter;


@Setter @Getter
public class ParcelSummaryForRequestResponse {

	private Long id;
	
	private String description;
	
	private BigDecimal weight;
	
	private ParcelCategory category;
	
	private boolean fragile;
	
	private String photoUrl;
	
	
	
	
}
