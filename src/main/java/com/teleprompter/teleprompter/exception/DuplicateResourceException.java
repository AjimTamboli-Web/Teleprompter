package com.teleprompter.teleprompter.exception;

import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class DuplicateResourceException extends RuntimeException{

	private final String fieldName;
	private final String resourceName;
	private final Object fieldValue;
	
	public DuplicateResourceException(String fieldName, String resourceName, Object fieldValue) {
		this.fieldName = fieldName;
		this.fieldValue = fieldValue;
		this.resourceName = resourceName;
	}
}
