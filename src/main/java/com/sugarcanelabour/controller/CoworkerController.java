package com.sugarcanelabour.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sugarcanelabour.entity.CommonLogin;
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
	
	 // Coworker registers a laborer
	@Operation(summary = "Register Laborer API", description = "This API is used to register a laborer by the coworker")
    @PostMapping("/register-laborer")
    public ResponseEntity<ApiResponse<Map<String, Object>>> registerLaborer(
            @Valid @RequestBody RegistrationDto laborerDto) {
        return commonLoginService.registerLaborer(laborerDto);
    }
}