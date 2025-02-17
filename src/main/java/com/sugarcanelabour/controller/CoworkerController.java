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
import org.springframework.web.bind.annotation.RestController;

import com.sugarcanelabour.helper.ApiResponse;
import com.sugarcanelabour.helper.CommonFunctions;
import com.sugarcanelabour.model.RegistrationDto;
import com.sugarcanelabour.service.CommonLoginService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/sclm/coworker")
@Validated
public class CoworkerController {

	private CommonLoginService commonLoginService;
	private CommonFunctions commonFunctions;

	public CoworkerController(CommonLoginService commonLoginService, CommonFunctions commonFunctions) {
		super();
		this.commonLoginService = commonLoginService;
		this.commonFunctions = commonFunctions;
	}

	@PostMapping("/register-labour")
	public ResponseEntity<ApiResponse<Map<String, Object>>> registerLabor(HttpServletRequest request,
			@ModelAttribute RegistrationDto registrationDto)// Use @ModelAttribute to bind the form data to DTO
	{
		if (log.isInfoEnabled()) {
			log.info("***** Inside CoworkerController - registerLabor *****");
		}
		Long cowId = commonFunctions.getUserIdFromRequest(request);
		return commonLoginService.registerLabor(registrationDto, cowId);
	}

	@PutMapping(value = "/update-labor/{commonLoginId}")
	public ResponseEntity<Object> updateLaborDetails(@PathVariable Long commonLoginId,
			@ModelAttribute RegistrationDto laborUpdateRequest) {
		return commonLoginService.updateLaborDetails(commonLoginId, laborUpdateRequest);
	}

	@DeleteMapping("/delete/labor/{commonLoginId}")
	public ResponseEntity<ApiResponse<Map<String, Object>>> deleteLabor(@PathVariable Long commonLoginId) {
		if (log.isInfoEnabled()) {
			log.info("***** Inside CoworkerController - deleteLabor *****");
		}
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
