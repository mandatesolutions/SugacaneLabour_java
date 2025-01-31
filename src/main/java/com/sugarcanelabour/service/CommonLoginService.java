package com.sugarcanelabour.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;

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

	ResponseEntity<ApiResponse<Map<String, Object>>> registerCoworker(RegistrationDto coworkerDto);

	ResponseEntity<ApiResponse<Map<String, Object>>> registerLaborer(RegistrationDto laborerDto);

	// CommonLogin registerSupervisor(RegistrationDto registrationDto);

}
