package com.sugarcanelabour.controller.SuperAdmin;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sugarcanelabour.helper.ApiResponse;
import com.sugarcanelabour.model.SuperAdminRegistrationDto;
import com.sugarcanelabour.service.CommonLoginService;
import com.sugarcanelabour.service.SuperAdminService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/sclm/sup-admin")
@Slf4j
@Validated
public class SuperAdminController {

	private CommonLoginService commonLoginService;
	private SuperAdminService adminService;

	public SuperAdminController(CommonLoginService commonLoginService,SuperAdminService adminService) {
		this.commonLoginService = commonLoginService;
		this.adminService=adminService;
	}

	@Operation(summary = "Register Super Admin API", description = "This API is used to register a super admin")
	@PostMapping("/register")
	ResponseEntity<ApiResponse<Map<String, Object>>> registerSuperAdmin(
			@Valid @RequestBody SuperAdminRegistrationDto superAdminDto) {
		if (log.isInfoEnabled()) {
			log.info("***** Inside SuperAdminController - registerSuperAdmin *****");
		}
		return commonLoginService.registerSuperAdmin(superAdminDto);
	}

	@GetMapping("/test")
	ResponseEntity<Object> test() {
		log.info("***** Inside - UserController - test *****");
		Map<String, Object> response = new HashMap<>();
		response.put("status", "Hello");
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@Operation(summary = "Get Admin Details API", description = "This API is used to get the details of an admin")
	@GetMapping("/getAdminDetails/{userId}")
	public ResponseEntity<ApiResponse<Map<String, Object>>> getAdminDetails(@PathVariable Long userId) {
	    log.info("***** Inside - SuperAdminController - getAdminDetails *****");

	    // Call the service method to fetch admin details
	    ResponseEntity<ApiResponse<Map<String, Object>>> response = adminService.getAdminDetails(userId);

	    // Return the response from the service
	    return response;
	}

	@Operation(summary = "Get Supervisor Details API", description = "This API is used to get the details of a supervisor")
	@GetMapping("/getSupervisorDetails/{userId}")
	public ResponseEntity<ApiResponse<Map<String, Object>>> getSupervisorDetails(@PathVariable Long userId) {
	    log.info("***** Inside - SuperAdminController - getSupervisorDetails *****");

	    // Call the service method to fetch supervisor details
	    ResponseEntity<ApiResponse<Map<String, Object>>> response = adminService.getSupervisorDetails(userId);

	    // Return the response from the service
	    return response;
	}

	
	  
	  @Operation(summary = "Get Coworker Details API", description = "This API is used to get the details of a coworker")
	  @GetMapping("/getCoworkerDetails/{commonLoginId}")
	  public ResponseEntity<ApiResponse<Map<String, Object>>> getCoworkerDetails(@PathVariable Long commonLoginId) {
	      log.info("***** Inside - SuperAdminController - getCoworkerDetails *****");
	      
	      // Call the service method to fetch coworker details
	      ResponseEntity<ApiResponse<Map<String, Object>>> response = adminService.getCoworkerDetails(commonLoginId);
	      
	      // Return the response from the service
	      return response;
	  }
	  
	  


	  @Operation(summary = "Get Labor Details API", description = "This API is used to get the details of a laborer")
	  @GetMapping("/getLaborDetails/{commonLoginId}")
	  public ResponseEntity<ApiResponse<Map<String, Object>>> getLaborDetails(@PathVariable Long commonLoginId) {
	      log.info("***** Inside - SuperAdminController - getLaborDetails *****");

	      // Calling the service layer to fetch labor details using the provided commonLoginId
	      ResponseEntity<ApiResponse<Map<String, Object>>> response = adminService.getLaborDetails(commonLoginId);

	      // Returning the response as it is from the service
	      return response;
	  }

	  //deactivate user
	  
	  @Operation(summary = "Deactivate User API", description = "This API is used to deactivate an admin, supervisor, or coworker.")
	  @DeleteMapping("/deactivateUser/{userId}")
	  public ResponseEntity<ApiResponse<Map<String, Object>>> deactivateUser(@PathVariable Long userId) {
	      log.info("***** Inside - SuperAdminController - deactivateUser *****");

	      // Call the service method to deactivate user
	      ResponseEntity<ApiResponse<Map<String, Object>>> response = adminService.deactivateUser(userId);

	      // Return the response from the service
	      return response;
	  }


}
