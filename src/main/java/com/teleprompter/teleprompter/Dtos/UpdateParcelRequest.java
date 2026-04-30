package com.teleprompter.teleprompter.dtos;

import java.math.BigDecimal;
import com.teleprompter.teleprompter.enums.ParcelCategory;
import jakarta.validation.constraints.DecimalMin;
import lombok.Getter;
import lombok.Setter;


@Getter    @Setter
public class UpdateParcelRequest {

	
	private String description;
	
	private ParcelCategory category;
	
	private String photoUrl;

	private Boolean fragile;

	@DecimalMin(value = "0.01")
	private BigDecimal weight;

	@DecimalMin(value = "0.0", inclusive = false)
	private BigDecimal value;
	
	private Boolean restrictedItemsDeclared;
	
	
}
