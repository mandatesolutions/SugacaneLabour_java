package com.sugarcanelabour.exception;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;

public class AuthenticationException extends RuntimeException {
	public AuthenticationException(String message, JwtException e) {
		super(message);
	}

}
