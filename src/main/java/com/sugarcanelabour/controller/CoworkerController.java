package com.sugarcanelabour.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.sugarcanelabour.helper.ApiResponse;
import com.sugarcanelabour.helper.CommonFunctions;
import com.sugarcanelabour.model.RegistrationDto;
import com.sugarcanelabour.service.CommonLoginService;

import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/sclm/coworker")
@Validated
public class CoworkerController {
	
	private CommonLoginService commonLoginService;
	private CommonFunctions commonFunctions;
	
	
	
	 public CoworkerController(CommonLoginService commonLoginService,CommonFunctions commonFunction) {
		this.commonLoginService = commonLoginService;
		this.commonFunctions=commonFunctions;
	}


//	// Coworker registers a laborer
//	@Operation(summary = "Register Laborer API", description = "This API is used to register a laborer by the coworker")
//	@PostMapping("/register-laborer")
//	public ResponseEntity<ApiResponse<Map<String, Object>>> registerLaborer(
//	        @Valid @RequestBody RegistrationDto laborDto) {
//	    return commonLoginService.registerLabor(laborDto);
//	}

//	 @PostMapping("/register-laborer")
//	 public ResponseEntity<ApiResponse<Map<String, Object>>> registerLabor(
//			 @ModelAttribute @RequestParam("profileImage") MultipartFile profileImage, 
//	         @Valid @RequestBody RegistrationDto registrationDto) {
//	     return commonLoginService.registerLabor(registrationDto, profileImage);
//	 }

//	 @PostMapping("/register-laborer")
//	 public ResponseEntity<ApiResponse<Map<String, Object>>> registerLabor(
//	         @ModelAttribute RegistrationDto registrationDto, // Use @ModelAttribute to bind the form data to DTO
//	         @RequestParam("profileImage") MultipartFile profileImage) { // Use @RequestParam for file
//	     return commonLoginService.registerLabor(registrationDto, profileImage);
//	 }
	 
	 @PostMapping("/register-laborer")
	 public ResponseEntity<ApiResponse<Map<String, Object>>> registerLabor(@ModelAttribute RegistrationDto registrationDto )// Use @ModelAttribute to bind the form data to DTO
	 { // Use @RequestParam for file
	     return commonLoginService.registerLabor(registrationDto);
	 }


	 
	 @PutMapping(value = "/update-labor/{commonLoginId}")
	 public ResponseEntity<Object> updateLaborDetails(
	         @PathVariable Long commonLoginId,
	         @ModelAttribute RegistrationDto laborUpdateRequest) {

	     return commonLoginService.updateLaborDetails(commonLoginId, laborUpdateRequest);
	 }


	
	
	 // Delete labor by ID (only accessible by Co-workers, handled in security config)
    @DeleteMapping("/delete/labor/{commonLoginId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> deleteLabor(@PathVariable Long commonLoginId) {
        // Call the service method to delete labor details
        return commonLoginService.deleteLaborDetails(commonLoginId);
    }
    
	
	
	@GetMapping("/tests")
	ResponseEntity<Object> test() {
		log.info("***** Inside - Coworker Controller - test *****");
		Map<String, Object> response = new HashMap<>();
		response.put("status", "success");
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