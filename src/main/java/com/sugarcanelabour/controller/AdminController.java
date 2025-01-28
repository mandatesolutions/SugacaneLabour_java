package com.sugarcanelabour.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sugarcanelabour.entity.CommonLogin;
import com.sugarcanelabour.model.RegistrationDto;
import com.sugarcanelabour.service.CommonLoginService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;



@RestController
@RequestMapping("/sclm/admin")
@Slf4j
public class AdminController {
	
	 @Autowired
	    private CommonLoginService commonLoginService;

	    // Endpoint for Admin to register a Supervisor
	   // Admin registers a supervisor
	 // Admin registers a supervisor
	 @Operation(summary = "Admin Register Supervisor API", description = "This API is used to register a supervisor")
	 @PostMapping("/register-supervisor")
	 public ResponseEntity<Object> registerSupervisor(@Valid @RequestBody RegistrationDto registrationDto) {
	     log.info("***** Inside - SupervisorController - registerSupervisor *****");
	     
	     // Call the service to register the supervisor
	     CommonLogin commonLogin = commonLoginService.registerSupervisor(registrationDto);

	     // Return the response
	     return ResponseEntity.status(HttpStatus.CREATED).body(commonLogin);
	 }
}
