package com.sugarcanelabour.controller.SuperAdmin;

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
	  public ResponseEntity<String> registerSuperAdmin(@RequestBody SuperAdminRegistrationDto superAdminDto) {
	      try {
	          CommonLogin newSuperAdmin = commonLoginService.registerSuperAdmin(superAdminDto);
	          return ResponseEntity.ok("Super Admin registered successfully");
	      } catch (Exception e) {
	          return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Super Admin registration failed: " + e.getMessage());
	      }
	  }

}
