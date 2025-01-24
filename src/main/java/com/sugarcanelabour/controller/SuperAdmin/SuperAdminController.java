package com.sugarcanelabour.controller.SuperAdmin;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sugarcanelabour.entity.CommonLogin;
import com.sugarcanelabour.model.SuperAdminRegistrationDto;
import com.sugarcanelabour.service.CommonLoginService;

import io.swagger.v3.oas.annotations.parameters.RequestBody;

@RestController
@RequestMapping("/sclm/admin")
public class SuperAdminController {

	@Autowired
	private CommonLoginService commonLoginService;

	@PostMapping("/super-admin/register")
	public ResponseEntity<Object> registerSuperAdmin(@RequestBody SuperAdminRegistrationDto superAdminDto) {
	    try {
	        return commonLoginService.registerSuperAdmin(superAdminDto);
	    } catch (Exception e) {
	        Map<String, Object> response = new HashMap<>();
	        response.put("status", "failure");
	        response.put("message", "Super Admin registration failed: " + e.getMessage());
	        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	    }
	}


	  

}
