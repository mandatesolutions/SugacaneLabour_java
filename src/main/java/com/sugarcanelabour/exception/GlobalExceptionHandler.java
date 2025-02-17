package com.sugarcanelabour.exception;

import java.util.Date;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@ControllerAdvice
public class GlobalExceptionHandler {
	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<Object> resourceNotFoundException(ResourceNotFoundException ex, WebRequest request) {
		ErrorResponse errorDetails = new ErrorResponse(new Date(), ex.getMessage(), HttpStatus.NOT_FOUND.toString(),
				"Something went wrong");
		return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(UserInactiveException.class)
	public ResponseEntity<Object> userInactiveException(UserInactiveException ex, WebRequest request) {
		ErrorResponse errorDetails = new ErrorResponse(new Date(), ex.getMessage(), HttpStatus.BAD_REQUEST.toString(),
				"Something went wrong");
		return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<Object> globalExceptionHandler(Exception ex, WebRequest request) {
		ErrorResponse errorDetails = new ErrorResponse(new Date(), ex.getMessage(),
				HttpStatus.INTERNAL_SERVER_ERROR.toString(), "Something went wrong");
		return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Object> methodArgumentNotValidException(MethodArgumentNotValidException ex,
			WebRequest request) {
		ErrorResponse errorDetails = new ErrorResponse(new Date(),
				ex.getBindingResult().getFieldError().getDefaultMessage(), HttpStatus.BAD_REQUEST.toString(),
				"Validation error");
		return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(AuthenticationException.class)
	public ResponseEntity<Object> handleAuthenticationException(AuthenticationException ex, WebRequest request) {
		ErrorResponse errorDetails = new ErrorResponse(new Date(), ex.getMessage(), HttpStatus.UNAUTHORIZED.toString(),
				"Invalid token");
		return new ResponseEntity<>(errorDetails, HttpStatus.UNAUTHORIZED);
	}

	@ExceptionHandler(MaxUploadSizeExceededException.class)
	public ResponseEntity<Object> handleMaxSizeException(MaxUploadSizeExceededException ex, WebRequest request) {

		// Construct the error message without using String.format()
		String errorMessage = "File size exceeds the maximum limit";

		// Create the error response
		ErrorResponse errorDetails = new ErrorResponse(new Date(), errorMessage, HttpStatus.BAD_REQUEST.toString(),
				"File upload error");
		return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(ConflictException.class)
	public ResponseEntity<Object> handleConflictException(ConflictException ex, WebRequest request) {
		ErrorResponse errorDetails = new ErrorResponse(new Date(), ex.getMessage(), HttpStatus.NOT_FOUND.toString(),
				"Something went wrong");
		return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
	}

}
