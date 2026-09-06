package com.teleprompter.teleprompter.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

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

	@ExceptionHandler(InvalidCredentialsException.class) // new 401 handler
	public ResponseEntity<ErrorResponse> handleInvalidCredentials(InvalidCredentialsException ex,
			HttpServletRequest request) {

		return buildErrorResponse(HttpStatus.UNAUTHORIZED, ex.getMessage(), request);
	}

	@ExceptionHandler(BusinessRuleViolationException.class)
	public ResponseEntity<ErrorResponse> handleBusinessRuleViolation(BusinessRuleViolationException ex,
			HttpServletRequest request) {

		return buildErrorResponse(HttpStatus.UNPROCESSABLE_CONTENT, ex.getMessage(), request);
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
			HttpServletRequest request) {

		// own fix instead of ex.getMessage() for security,
		String safeMessage = "Required request body is missing or contains invalid data formats.";

		return buildErrorResponse(HttpStatus.BAD_REQUEST, safeMessage, request);
	}

	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex,
			HttpServletRequest request) {

		String safeMessage = String.format("The parameter '%s' contains an invalid data format.", ex.getName());

		return buildErrorResponse(HttpStatus.BAD_REQUEST, safeMessage, request);
	}

	@ExceptionHandler(MissingServletRequestParameterException.class)
	public ResponseEntity<ErrorResponse> handleMissingServletRequestParameter(
			MissingServletRequestParameterException ex, HttpServletRequest request) {

		String safeMessage = "Required request parameter '" + ex.getParameterName() + "' is missing.";

		return buildErrorResponse(HttpStatus.BAD_REQUEST, safeMessage, request);

	}

	// extracted private method / rule of DRY
	private ResponseEntity<ErrorResponse> buildErrorResponse(HttpStatus status, String message,
			HttpServletRequest request) {

		ErrorResponse errorResponse = new ErrorResponse(LocalDateTime.now(), status.value(), status.getReasonPhrase(),
				message, request.getRequestURI());

		return new ResponseEntity<>(errorResponse, status);
	}

	/**
	 * [Handler 1]: Handles validated request bodies ( @Valid @RequestBody).
	 * Refactored to reuse the private helper method.
	 */
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ValidationErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			HttpServletRequest request) {

		Map<String, String> errors = new HashMap<>();

		for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
			errors.put(fieldError.getField(), fieldError.getDefaultMessage());
		}

		return handleValidationErrorResponse(errors, request);
	}

	/**
	 * [Handler 2 - NEW]: Handles validated controller method parameters
	 * ( @Min, @Max). Extracts multi-field errors dynamically and maps to a uniform structure.
	 */
	@ExceptionHandler(HandlerMethodValidationException.class)
	public ResponseEntity<ValidationErrorResponse> handleHandlerMethodValidation(HandlerMethodValidationException ex,
			HttpServletRequest request) {

		Map<String, String> errors = new HashMap<>();

		// Loop over each parameter that failed validation rules
		ex.getParameterValidationResults().forEach(result -> {
			String parameterName = result.getMethodParameter().getParameterName();

			// Collect all resolvable constraint errors for this specific parameter
			result.getResolvableErrors().forEach(error -> {
				errors.put(parameterName, error.getDefaultMessage());
			});
		});

		return handleValidationErrorResponse(errors, request);

	}

	// PRIVATE VALIDATION HELPER (DRY Compliance - Extracted at the absolute bottom)
	private ResponseEntity<ValidationErrorResponse> handleValidationErrorResponse(Map<String, String> fieldErrors,
			HttpServletRequest request) {

		// Enforces our uniform 400 Bad Request shape across all validation violations
		ValidationErrorResponse response = new ValidationErrorResponse(LocalDateTime.now(),
				HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), fieldErrors,
				request.getRequestURI());

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
	}

}
