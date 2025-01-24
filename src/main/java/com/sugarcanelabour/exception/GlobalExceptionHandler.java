//package com.sugarcanelabour.exception;
////
////import java.util.HashMap;
////import java.util.List;
////import java.util.Map;
////import java.util.stream.Collectors;
////
////import org.springframework.http.HttpStatus;
////import org.springframework.http.ResponseEntity;
////import org.springframework.web.bind.MethodArgumentNotValidException;
////import org.springframework.web.bind.annotation.ControllerAdvice;
////import org.springframework.web.bind.annotation.ExceptionHandler;
////
//
//import java.util.Date;
//
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.MethodArgumentNotValidException;
//import org.springframework.web.bind.annotation.ControllerAdvice;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//import org.springframework.web.context.request.WebRequest;
//
//
//@ControllerAdvice
//public class GlobalExceptionHandler {
////	 @ExceptionHandler(MethodArgumentNotValidException.class)
////	    public ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException ex) {
////	        Map<String, Object> response = new HashMap<>();
////	        response.put("status", "failure");
////	        response.put("message", "Validation failed");
////
////	        List<String> errors = ex.getBindingResult().getFieldErrors().stream()
////	                                .map(fieldError -> fieldError.getDefaultMessage())
////	                                .collect(Collectors.toList());
////
////	        response.put("errors", errors);
////
////	        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
////	    }
//	
//	@ExceptionHandler(MethodArgumentNotValidException.class)
//	public ResponseEntity<Object> methodArgumentNotValidException(MethodArgumentNotValidException ex,
//			WebRequest request) {
//		ErrorResponse errorDetails = new ErrorResponse(new Date(),
//				ex.getBindingResult().getFieldError().getDefaultMessage(), HttpStatus.BAD_REQUEST.toString(),
//				"Validation error");
//		return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
//	}
//
//}
