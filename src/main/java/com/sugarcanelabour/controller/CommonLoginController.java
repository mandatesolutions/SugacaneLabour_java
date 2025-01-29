package com.sugarcanelabour.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sugarcanelabour.entity.CommonLogin;
import com.sugarcanelabour.helper.ApiResponse;
import com.sugarcanelabour.model.LoginRequest;
import com.sugarcanelabour.model.RegistrationDto;
import com.sugarcanelabour.service.CommonLoginService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/sclm/common-login")
public class CommonLoginController {

	@Autowired
	private CommonLoginService loginService;

//	@PostMapping("/login")
//	public ResponseEntity<String> login(@RequestBody LoginRequest request) {
//	    System.out.println("Login attempt with email: " + request.getEmail()); // Debug print
//	    try {
//	        String result = loginService.login(request.getEmail(), request.getPassword());
//	        return ResponseEntity.ok(result);
//	    } catch (IllegalArgumentException e) {
//	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
//	    }
//	}

	@Operation(summary = "User Login API", description = "This API allows users to log in with their credentials.")
	@PostMapping("/login")
	ResponseEntity<ApiResponse<Map<String, Object>>> login(@Valid @RequestBody LoginRequest request)
			throws Exception {
		log.info("***** Inside SuperAdminController - login *****");
		return loginService.login(request);
	}

//	  @PostMapping("/register-supervisor")
//	    public ResponseEntity<CommonLogin> registerSupervisor(@RequestBody SupervisorRegistrationDto supervisorDto) {
//	        CommonLogin registeredSupervisor = loginService.registerSupervisor(supervisorDto);
//	        return ResponseEntity.status(HttpStatus.CREATED).body(registeredSupervisor);
//	    }
}
