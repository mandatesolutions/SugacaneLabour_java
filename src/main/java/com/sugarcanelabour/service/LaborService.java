package com.sugarcanelabour.service;

import java.util.Map;

import org.springframework.http.ResponseEntity;

import com.sugarcanelabour.helper.ApiResponse;

public interface LaborService {


	ResponseEntity<ApiResponse<Map<String, Object>>> getLaborDetails(Long commonLoginId);

	

}
