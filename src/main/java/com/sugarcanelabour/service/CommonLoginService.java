package com.sugarcanelabour.service;

import java.util.Map;

import org.springframework.http.ResponseEntity;

import com.sugarcanelabour.entity.CommonLogin;
import com.sugarcanelabour.model.RegistrationDto;
import com.sugarcanelabour.model.SuperAdminRegistrationDto;

public interface CommonLoginService {

	String login(String email, String password);



	//CommonLogin registerSupervisor(RegistrationDto supervisorDto, String role);

	CommonLogin registerSupervisor(RegistrationDto supervisorDto);



	ResponseEntity<Object> registerSuperAdmin(SuperAdminRegistrationDto superAdminDto);



	//CommonLogin registerSupervisor(RegistrationDto registrationDto);

	



}
