package com.sugarcanelabour.helper;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class JwatResponse {
	String token;

	public JwatResponse() {
		
	}

	public JwatResponse(String token) {
		this.token = token;
	}
	

}
