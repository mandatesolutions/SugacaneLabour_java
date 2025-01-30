package com.sugarcanelabour.helper;

import java.util.Map;

import com.sugarcanelabour.model.RegistrationDto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
	private static final long serialVersionUID = 1L;
	private String status;
	private String message;
	private T data;
	public ApiResponse(T data) {
		super();
		this.data = data;
	}

	
}
