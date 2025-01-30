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

<<<<<<< HEAD
//	ResponseEntity<Object> registerSupervisor(@Valid RegistrationDto supervisorDto);
//	
//
//	ResponseEntity<Object> registerCoworker(@Valid RegistrationDto coworkerDto);
//
//
//	ResponseEntity<Object> registerLaborer(@Valid RegistrationDto laborerDto);


	ResponseEntity<Object> registerSupervisor(SupervisorDetails supervisorDetails);
=======
	ResponseEntity<Object> registerSupervisor(@Valid RegistrationDto supervisorDto);

	ResponseEntity<Object> registerCoworker(@Valid RegistrationDto coworkerDto);

	ResponseEntity<Object> registerLaborer(@Valid RegistrationDto laborerDto);
>>>>>>> 962f4c893e3c115ff02c6978a4457416930bd1d7

	// CommonLogin registerSupervisor(RegistrationDto registrationDto);

}
