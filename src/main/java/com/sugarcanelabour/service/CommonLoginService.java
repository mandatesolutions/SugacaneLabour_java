package com.sugarcanelabour.service;

import java.util.Map;

import org.springframework.http.ResponseEntity;

import com.sugarcanelabour.entity.CommonLogin;
import com.sugarcanelabour.model.LoginRequest;
import com.sugarcanelabour.model.RegistrationDto;
import com.sugarcanelabour.model.SuperAdminRegistrationDto;

import jakarta.validation.Valid;

public interface CommonLoginService {

	

	ResponseEntity<Object> login(@Valid LoginRequest loginRequest) throws Exception;


	ResponseEntity<Object> registerSuperAdmin(@Valid SuperAdminRegistrationDto superAdminDto);


	ResponseEntity<Object> registerSupervisor(@Valid RegistrationDto supervisorDto);
	

	ResponseEntity<Object> registerCoworker(@Valid RegistrationDto coworkerDto);


	ResponseEntity<Object> registerLaborer(@Valid RegistrationDto laborerDto);















	




	//CommonLogin registerSupervisor(RegistrationDto registrationDto);

	



}
