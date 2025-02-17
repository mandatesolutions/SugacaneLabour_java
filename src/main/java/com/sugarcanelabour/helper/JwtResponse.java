package com.sugarcanelabour.helper;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class JwtResponse {
	String token;

	public JwtResponse() {
		
	}

	public JwtResponse(String token) {
		super();
		this.token = token;
	}
	

}
