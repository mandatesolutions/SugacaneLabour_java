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

import com.sugarcanelabour.helper.ApiResponse;
import com.sugarcanelabour.helper.CommonMessages;
import com.sugarcanelabour.model.RegistrationDto;
import com.sugarcanelabour.service.CommonLoginService;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/sclm/admin")
@Slf4j
public class AdminController {

	@Autowired
	private CommonLoginService commonLoginService;
	@Autowired
	private CommonMessages msg;

	// Admin registers a supervisor
	  @PostMapping("/register-supervisor")
	    public ResponseEntity<ApiResponse<Map<String, Object>>> registerSupervisor(
	            @Valid @RequestBody RegistrationDto registrationDto) {
	        return commonLoginService.registerSupervisor(registrationDto);
	    }

	@GetMapping("/tests")
	ResponseEntity<Object> test() {
		log.info("***** Inside - CommonLogin Controller - test *****");
		Map<String, Object> response = new HashMap<>();
		response.put("status",msg.SUCCESS );
		response.put("message", msg.TEST_MESSAGE);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
}
