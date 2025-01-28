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
@Slf4j
@RequestMapping("/sclm/supervisor")
public class SupervisorController {
	
	@Autowired
	private CommonLoginService commonLoginService;
//	
//	@Operation(summary = "Register Coworker API", description = "This API is used to register a coworker by a supervisor")
//	@PostMapping("/register-coworker")
//	ResponseEntity<Object> registerCoworker(@Valid @RequestBody RegistrationDto registrationDto) throws Exception{
//	    log.info("***** Inside - CoworkerController - registerCoworker *****");
//	    return ResponseEntity.ok(commonLoginService.registerCoworker(registrationDto));
//	}

}
