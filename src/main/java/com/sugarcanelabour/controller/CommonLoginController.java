package com.sugarcanelabour.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sugarcanelabour.Model.LoginRequest;
import com.sugarcanelabour.Model.RegistrationDto;
import com.sugarcanelabour.entity.CommonLogin;
import com.sugarcanelabour.service.CommonLoginService;

@RestController
@RequestMapping("/sclm/Controller")
public class CommonLoginController {

	
	@Autowired
    private CommonLoginService loginService;
    
	@PostMapping("/login")
	public ResponseEntity<String> login(@RequestBody LoginRequest request) {
	    System.out.println("Login attempt with email: " + request.getEmail()); // Debug print
	    try {
	        String result = loginService.login(request.getEmail(), request.getPassword());
	        return ResponseEntity.ok(result);
	    } catch (IllegalArgumentException e) {
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
	    }
	}
	 
//	  @PostMapping("/register-supervisor")
//	    public ResponseEntity<CommonLogin> registerSupervisor(@RequestBody SupervisorRegistrationDto supervisorDto) {
//	        CommonLogin registeredSupervisor = loginService.registerSupervisor(supervisorDto);
//	        return ResponseEntity.status(HttpStatus.CREATED).body(registeredSupervisor);
//	    }
}
