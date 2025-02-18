package com.sugarcanelabour.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sugarcanelabour.helper.ApiResponse;
import com.sugarcanelabour.helper.CommonMessages;
import com.sugarcanelabour.model.LaboursDto;
import com.sugarcanelabour.model.RegistrationDto;
import com.sugarcanelabour.service.AdminService;
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
	@Autowired
	private CommonMessages msg;
	
	@Autowired
	private AdminService adminService;

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
	
	 @Operation(summary = "Deactivate User API", description = "This API is used to deactivate an admin, supervisor, or coworker.")
	  @DeleteMapping("/deactivateUser/{userId}")
	  public ResponseEntity<ApiResponse<Map<String, Object>>> deactivateUser(@PathVariable Long userId) {
	      log.info("***** Inside - SuperAdminController - deactivateUser *****");

	      // Call the service method to deactivate user
	      ResponseEntity<ApiResponse<Map<String, Object>>> response = commonLoginService.deactivateUser(userId);

	      // Return the response from the service
	      return response;
	  }
	 
	 //    ****COUNT******
	 
	  @Operation(summary = "Get User Count For All Roles", description = "This API retrieves the count of users for all roles.")
	  @GetMapping("/getUserCountByAllRoles")
	  public ResponseEntity<ApiResponse<Map<String, Object>>> getUserCountByAllRoles() {
	      return commonLoginService.getCountByAllRoles();
	  }
	  
	  //   ******* REGISTRATION BY MONTH *********
	  @GetMapping("/registrations-by-month")
	    public ResponseEntity<ApiResponse<Map<String, Object>>> getRegistrationCountByMonth() {
	        return commonLoginService.getCountByMonth();
	    }
	  
	  //****TOP 10 LABORS ******

	  @Operation(summary = "Get Latest 10 Labors", description = "Fetches the latest 10 registered labors.")
	    @GetMapping("/getLatestLabors")
	    public ResponseEntity<ApiResponse<List<LaboursDto>>> getLatestLaborsDetails() {
	        return commonLoginService.getLatestLaborDetails();
	    }
	  
	  
	  //****GET ALL ****
	  @GetMapping("/getAllSupervisors")
	  public ResponseEntity<ApiResponse<Map<String, Object>>> getAllSupervisors() 
	  {
		  log.info("***** Inside - SuperAdminController - getAllSupervisors *****");
		  return adminService.getAllSupervisors();
	  }
	  
	  @GetMapping("/getAllCoworkers")
	  public ResponseEntity<ApiResponse<Map<String, Object>>> getAllCoworkers() 
	  {
		  log.info("***** Inside - SuperAdminController - getAllCoworkers *****");
		  return adminService.getAllCoworkers();

	  }
	  
	  @GetMapping("/getAllLabours")
	  public ResponseEntity<ApiResponse<Map<String, Object>>> getAllLabours() 
	  {
		  log.info("***** Inside - SuperAdminController - getAllLabours *****");
		  return adminService.getAllLabours();

	  }
}
