package com.sugarcanelabour.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import com.sugarcanelabour.entity.CommonLogin;
import com.sugarcanelabour.entity.SupervisorDetails;
import com.sugarcanelabour.helper.ApiResponse;
import com.sugarcanelabour.model.LoginRequest;
import com.sugarcanelabour.model.RegistrationDto;
import com.sugarcanelabour.model.SuperAdminRegistrationDto;

import jakarta.validation.Valid;

public interface CommonLoginService {

	ResponseEntity<ApiResponse<Map<String, Object>>> login(@Valid LoginRequest loginRequest) throws Exception;

	ResponseEntity<ApiResponse<Map<String, Object>>> registerSuperAdmin(@Valid SuperAdminRegistrationDto superAdminDto);

	ResponseEntity<ApiResponse<Map<String, Object>>> registerSupervisor(RegistrationDto registrationDto);

	ResponseEntity<ApiResponse<Map<String, Object>>> registerCoWorker(RegistrationDto registrationDto);

//	ResponseEntity<ApiResponse<Map<String, Object>>> registerLabor(RegistrationDto laborDto);

	ResponseEntity<Object> updateLaborDetails(Long commonLoginId, RegistrationDto laborUpdateRequest);

	ResponseEntity<ApiResponse<Map<String, Object>>> deleteLaborDetails(Long commonLoginId);

	
//	ResponseEntity<ApiResponse<Map<String, Object>>> registerLabor(RegistrationDto laborDto, MultipartFile profileImage);
	
	ResponseEntity<ApiResponse<Map<String, Object>>> registerLabor(RegistrationDto laborDto);

	ResponseEntity<ApiResponse<Map<String, Object>>> deactivateUser(Long userId);

	ResponseEntity<ApiResponse<List<Map<String, Object>>>> getAllLaborDetails();

	//ResponseEntity<ApiResponse<String>> updateLaborDetails(Long commonLoginId, RegistrationDto laborUpdateRequest);


	// CommonLogin registerSupervisor(RegistrationDto registrationDto);

}
