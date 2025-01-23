package com.sugarcanelabour.service;

import com.sugarcanelabour.Model.RegistrationDto;
import com.sugarcanelabour.Model.SuperAdminRegistrationDto;
import com.sugarcanelabour.entity.CommonLogin;

public interface CommonLoginService {

	String login(String email, String password);



	//CommonLogin registerSupervisor(RegistrationDto supervisorDto, String role);

	CommonLogin registerSupervisor(RegistrationDto supervisorDto);



	CommonLogin registerSuperAdmin(SuperAdminRegistrationDto superAdminDto);

	



}
