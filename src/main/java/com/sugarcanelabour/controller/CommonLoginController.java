package com.sugarcanelabour.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sugarcanelabour.Model.LoginRequest;
import com.sugarcanelabour.entity.CommonLogin;
import com.sugarcanelabour.service.CommonLoginService;

@RestController
@RequestMapping("/sclm/Controller")
public class CommonLoginController {

	
	@Autowired
    private CommonLoginService loginService;
    
	 @PostMapping
	    public ResponseEntity<String> login(@RequestBody LoginRequest request) {
	        try {
	            String result = loginService.login(request.getEmail(), request.getPassword());
	            return ResponseEntity.ok(result);
	        } catch (IllegalArgumentException e) {
	            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
	        }
	    }
}
