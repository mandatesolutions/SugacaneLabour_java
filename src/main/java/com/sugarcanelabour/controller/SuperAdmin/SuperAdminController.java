package com.sugarcanelabour.controller.SuperAdmin;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sugarcanelabour.entity.CommonLogin;
import com.sugarcanelabour.model.SuperAdminRegistrationDto;
import com.sugarcanelabour.service.CommonLoginService;

import io.swagger.v3.oas.annotations.Operation; 
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/sclm/admin")
@Slf4j
@Validated
public class SuperAdminController {

	
	    private CommonLoginService commonLoginService;
	    
	    

	  public SuperAdminController(CommonLoginService commonLoginService) {
			this.commonLoginService = commonLoginService;
		}



		@Operation(summary = "Super Admin Registration API", description = "This API is used to register a super admin")
		  @PostMapping("/super-admin/register")
		  public ResponseEntity<Object> registerSuperAdmin( @Valid @RequestBody SuperAdminRegistrationDto superAdminDto) {
		      log.info("***** Inside - SuperAdminController - registerSuperAdmin *****");
		      // Directly call the service layer for further processing
		      return ResponseEntity.ok(commonLoginService.registerSuperAdmin(superAdminDto));
		  }



}
