package com.sugarcanelabour.service;

import java.util.Map;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;

import com.sugarcanelabour.helper.ApiResponse;
import com.sugarcanelabour.model.RegistrationDto;

public interface LaborService {


	ResponseEntity<ApiResponse<Map<String, Object>>> getLaborDetails(Long commonLoginId);

	ResponseEntity<Map<String, Object>> generateLaborDetailsPdfAndSendEmail(Long commonLoginId, String recipientEmail);

	ResponseEntity<ApiResponse<String>> updateLaborDetails(Long commonLoginId,
			RegistrationDto laborUpdateRequest);

	
	



	

}
