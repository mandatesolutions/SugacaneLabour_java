package com.sugarcanelabour.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.sugarcanelabour.helper.ApiResponse;
import com.sugarcanelabour.service.LaborService;

import io.swagger.v3.oas.annotations.Operation;

@RestController
public class LabourController {
	@Autowired
	private LaborService laborService;


	  @Operation(summary = "Get Labor Information", description = "Fetches labor information including their personal details and uploaded documents.")
	    @GetMapping("/getLaborDetails/{commonLoginId}")
	    public ResponseEntity<ApiResponse<Map<String, Object>>> getLaborDetails(@PathVariable Long commonLoginId) {
	        return laborService.getLaborDetails(commonLoginId);
	    }
}
