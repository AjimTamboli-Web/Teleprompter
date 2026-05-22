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
	public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFoundException ex,HttpServletRequest request){
		
		HttpStatus status = HttpStatus.NOT_FOUND;
		
		ErrorResponse errorResponse = new ErrorResponse(
				LocalDateTime.now(),
				status.value(),
				status.getReasonPhrase(),
				ex.getMessage(),
				request.getRequestURI()
				);
		
		return new ResponseEntity<>(errorResponse,status);
	}
	
	@ExceptionHandler(DuplicateResourceException.class)
	public ResponseEntity<ErrorResponse> handleDuplicateResource(DuplicateResourceException ex,HttpServletRequest request){
		
		HttpStatus status = HttpStatus.CONFLICT;
		
		ErrorResponse errorResponse = new ErrorResponse(
				LocalDateTime.now(),
				status.value(),
				status.getReasonPhrase(),
				ex.getMessage(),
				request.getRequestURI()
				);
		
		return new ResponseEntity<>(errorResponse, status);
	}
	
}
