package com.teleprompter.teleprompter.exception;

import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ResourceNotFoundException  extends RuntimeException{

	private final String resourceName;
	private final String fieldName;
	private final Object fieldValue;
	
	public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
		this.fieldName = fieldName;
		this.fieldValue = fieldValue;
		this.resourceName = resourceName;
	}	
}
