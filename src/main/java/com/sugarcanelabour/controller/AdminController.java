package com.sugarcanelabour.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sugarcanelabour.entity.CommonLogin;
import com.sugarcanelabour.model.RegistrationDto;
import com.sugarcanelabour.service.CommonLoginService;

import io.swagger.v3.oas.annotations.parameters.RequestBody;

@RestController
@RequestMapping("/sclm/admin")
public class AdminController {
	
	 @Autowired
	    private CommonLoginService commonLoginService;

	    // Endpoint for Admin to register a Supervisor
	   // Admin registers a supervisor
	 // Admin registers a supervisor
	    @PostMapping("/register-supervisor")
	    public ResponseEntity<CommonLogin> registerSupervisor(@RequestBody RegistrationDto registrationDto) {
	        // Only admin can register a supervisor
	        CommonLogin commonLogin = commonLoginService.registerSupervisor(registrationDto);
	        return ResponseEntity.status(HttpStatus.CREATED).body(commonLogin);
	    }
}
