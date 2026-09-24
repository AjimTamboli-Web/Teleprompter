package com.teleprompter.teleprompter.dtos;

import java.math.BigDecimal;
import com.teleprompter.teleprompter.enums.ParcelCategory;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import lombok.Getter;
import lombok.Setter;


@Getter    @Setter
public class UpdateParcelRequest {

	
	private String description;
	
	private ParcelCategory category;
	
	private String photoUrl;

	private Boolean fragile;

	@DecimalMin(value = "1.00")
	@DecimalMax(value = "9999.99")
    @Digits(integer = 4, fraction = 2)
	private BigDecimal weight;

	@DecimalMax(value = "99999999.99")
	@DecimalMin(value = "0.0", inclusive = false)
	@Digits(integer = 8,fraction = 2)
	private BigDecimal value;
	
	private Boolean restrictedItemsDeclared;
	
}
