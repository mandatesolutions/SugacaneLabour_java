package com.sugarcanelabour.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sugarcanelabour.entity.CommonLogin;
import com.sugarcanelabour.entity.SupervisorDetails;
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

	// Admin registers a supervisor
	@Operation(summary = "Register Supervisor API", description = "This API is used to register a supervisor by the admin")
	@PostMapping("/register-supervisor")
	ResponseEntity<Object> registerSupervisor(@Valid @RequestBody SupervisorDetails supervisorDto) {
		return commonLoginService.registerSupervisor(supervisorDto);
	}

	@GetMapping("/tests")
	ResponseEntity<Object> test() {
		log.info("***** Inside - UserController - test *****");
		Map<String, Object> response = new HashMap<>();
		response.put("status", "Hello");
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
}
