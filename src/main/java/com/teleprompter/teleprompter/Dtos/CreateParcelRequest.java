package com.teleprompter.teleprompter.dtos;

import java.math.BigDecimal;
import java.util.UUID;

import com.teleprompter.teleprompter.enums.ParcelCategory;

import jakarta.validation.constraints.DecimalMin;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CreateParcelRequest {

	@NotNull
	private UUID senderId;
	
	@NotBlank
	private String description;
	
	@NotNull
	@DecimalMin("1")
	private BigDecimal weight;

	@NotNull
	@DecimalMin(value = "0.0", inclusive = false)
	private BigDecimal value;
	
	@NotNull
	private Boolean fragile;
	
	@NotNull
	private Boolean restrictedItemsDeclared;
	
	@NotBlank
	private String photoUrl;
	
	@NotNull
	private ParcelCategory category;
	

	
	
	public Boolean getFragile() {
		return fragile;
	}

	public void setFragile(Boolean fragile) {
		this.fragile = fragile;
	}

	public UUID getSenderId() {
		return senderId;
	}

	public void setSenderId(UUID senderId) {
		this.senderId = senderId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public BigDecimal getWeight() {
		return weight;
	}

	public void setWeight(BigDecimal weight) {
		this.weight = weight;
	}

	public BigDecimal getValue() {
		return value;
	}

	public void setValue(BigDecimal value) {
		this.value = value;
	}

	public Boolean getRestrictedItemsDeclared() {
		return restrictedItemsDeclared;
	}

	public void setRestrictedItemsDeclared(Boolean restrictedItemsDeclared) {
		this.restrictedItemsDeclared = restrictedItemsDeclared;
	}

	public String getPhotoUrl() {
		return photoUrl;
	}

	public void setPhotoUrl(String photoUrl) {
		this.photoUrl = photoUrl;
	}

	public ParcelCategory getCategory() {
		return category;
	}

	public void setCategory(ParcelCategory category) {
		this.category = category;
	}
	
	
}
