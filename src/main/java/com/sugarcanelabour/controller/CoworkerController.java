package com.sugarcanelabour.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sugarcanelabour.helper.ApiResponse;
import com.sugarcanelabour.model.RegistrationDto;
import com.sugarcanelabour.service.CommonLoginService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/sclm/coworker")
public class CoworkerController {
	
	private CommonLoginService commonLoginService;
	
	
	
	 public CoworkerController(CommonLoginService commonLoginService) {
		this.commonLoginService = commonLoginService;
	}


	// Coworker registers a laborer
	@Operation(summary = "Register Laborer API", description = "This API is used to register a laborer by the coworker")
	@PostMapping("/register-laborer")
	public ResponseEntity<ApiResponse<Map<String, Object>>> registerLaborer(
	        @Valid @RequestBody RegistrationDto laborDto) {
	    return commonLoginService.registerLabor(laborDto);
	}

	@PutMapping("/update/{commonLoginId}")
	public ResponseEntity<Object> updateLaborDetails(
	        @PathVariable("commonLoginId") Long commonLoginId, 
	        @RequestBody RegistrationDto laborUpdateRequest) {
	    
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
}