package com.sugarcanelabour.service;

import com.sugarcanelabour.entity.CommonLogin;
import com.sugarcanelabour.model.RegistrationDto;
import com.sugarcanelabour.model.SuperAdminRegistrationDto;

public interface CommonLoginService {

	String login(String email, String password);



	//CommonLogin registerSupervisor(RegistrationDto supervisorDto, String role);

	CommonLogin registerSupervisor(RegistrationDto supervisorDto);



	CommonLogin registerSuperAdmin(SuperAdminRegistrationDto superAdminDto);



	//CommonLogin registerSupervisor(RegistrationDto registrationDto);

	



}
