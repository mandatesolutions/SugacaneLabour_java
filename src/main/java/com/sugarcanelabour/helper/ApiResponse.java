package com.sugarcanelabour.helper;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse<T> {
	private String status;
	private String message;
	private T data;

	public ApiResponse(T data) {
		super();
		this.data = data;
	}

}
