package com.sugarcanelabour.service;

import java.util.Map;

import org.springframework.http.ResponseEntity;

import com.sugarcanelabour.entity.CommonLogin;
import com.sugarcanelabour.model.LoginRequest;
import com.sugarcanelabour.model.RegistrationDto;
import com.sugarcanelabour.model.SuperAdminRegistrationDto;

import jakarta.validation.Valid;

public interface CommonLoginService {

	

	//CommonLogin registerSupervisor(RegistrationDto supervisorDto, String role);

	


	//ResponseEntity<Object> registerSuperAdmin(SuperAdminRegistrationDto superAdminDto);



	ResponseEntity<Object> login(@Valid LoginRequest loginRequest) throws Exception;



	//Object registerCoworker(@Valid RegistrationDto registrationDto);



//	CommonLogin registerLaborer(@Valid RegistrationDto registrationDto);



	CommonLogin registerSupervisor(@Valid RegistrationDto registrationDto);















	




	//CommonLogin registerSupervisor(RegistrationDto registrationDto);

	



}
