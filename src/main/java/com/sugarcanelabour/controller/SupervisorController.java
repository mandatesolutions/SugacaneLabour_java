package com.sugarcanelabour.controller;

import java.util.HashMap;
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
import com.sugarcanelabour.helper.CommonFunctions;
import com.sugarcanelabour.model.RegistrationDto;
import com.sugarcanelabour.service.CommonLoginService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/sclm/supervisor")
public class SupervisorController {
	
	
	private CommonLoginService commonLoginService;
	private CommonFunctions commonFunctions;
	
	
	  public SupervisorController(CommonLoginService commonLoginService, CommonFunctions commonFunctions) {
		super();
		this.commonLoginService = commonLoginService;
		this.commonFunctions = commonFunctions;
	}

	@Operation(summary = "Register Coworker API", description = "This API is used to register a coworker by the supervisor")
	    @PostMapping("/register-coworker")
	    public ResponseEntity<ApiResponse<Map<String, Object>>> registerCoworker(
	           HttpServletRequest request, @Valid @RequestBody RegistrationDto registrationDto) {
		  Long supId = commonFunctions.getUserIdFromRequest(request);
	        return commonLoginService.registerCoWorker(registrationDto,supId);
	    }

	  @GetMapping("/tests")
		ResponseEntity<Object> test() {
			log.info("***** Inside supervisor Controller - test *****");
			Map<String, Object> response = new HashMap<>();
			response.put("status", "Success");
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
}

