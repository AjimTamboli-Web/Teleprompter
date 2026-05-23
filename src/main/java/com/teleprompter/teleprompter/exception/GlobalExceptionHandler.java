package com.teleprompter.teleprompter.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFoundException ex,
			HttpServletRequest request) {

		return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request);
	
	}

	@ExceptionHandler(DuplicateResourceException.class)
	public ResponseEntity<ErrorResponse> handleDuplicateResource(DuplicateResourceException ex,
			HttpServletRequest request) {

		return buildErrorResponse(HttpStatus.CONFLICT, ex.getMessage(), request);
	}
	
	// extracted private method /  rule of DRY 
	private ResponseEntity<ErrorResponse> buildErrorResponse(HttpStatus status, String message, HttpServletRequest request){
		
		ErrorResponse errorResponse = new ErrorResponse(LocalDateTime.now(),status.value(), status.getReasonPhrase(),
				message, request.getRequestURI());
		
		return new ResponseEntity<>(errorResponse,status);
	}

}
