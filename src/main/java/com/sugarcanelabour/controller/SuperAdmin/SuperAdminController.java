package com.sugarcanelabour.controller.SuperAdmin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sugarcanelabour.Model.SuperAdminRegistrationDto;
import com.sugarcanelabour.entity.CommonLogin;
import com.sugarcanelabour.service.CommonLoginService;

import io.swagger.v3.oas.annotations.parameters.RequestBody;

@RestController
@RequestMapping("/super-admin")
public class SuperAdminController {

	  @Autowired
	    private CommonLoginService commonLoginService;

	    @PostMapping("/register")
	    public ResponseEntity<String> registerSuperAdmin(@RequestBody SuperAdminRegistrationDto superAdminDto) {
	        try {
	            // Register the Super Admin
	            CommonLogin savedSuperAdmin = commonLoginService.registerSuperAdmin(superAdminDto);

	            // Return success response
	            return new ResponseEntity<>("Super Admin registered successfully", HttpStatus.CREATED);
	        } catch (Exception e) {
	            return new ResponseEntity<>("Super Admin registration failed: " + e.getMessage(), HttpStatus.BAD_REQUEST);
	        }
	    }
}
