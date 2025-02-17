package com.sugarcanelabour.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sugarcanelabour.helper.ApiResponse;
import com.sugarcanelabour.model.RegistrationDto;
import com.sugarcanelabour.service.LaborService;

import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/sclm/labor")
public class LabourController {
	@Autowired
	private LaborService laborService;


	  @Operation(summary = "Get Labor Information", description = "Fetches labor information including their personal details and uploaded documents.")
	    @GetMapping("/getLaborDetails/{commonLoginId}")
	    public ResponseEntity<ApiResponse<Map<String, Object>>> getLaborDetails(@PathVariable Long commonLoginId) {
	        return laborService.getLaborDetails(commonLoginId);
}
	  
	  
	  @GetMapping("/api/labor/generate-pdf/{commonLoginId}/email")
	  public ResponseEntity<Map<String, Object>> generateLaborDetailsPdfAndSendEmail(
	          @PathVariable Long commonLoginId, @RequestParam String recipientEmail) {

	      // Call the service method that generates the PDF and sends the email
	      return laborService.generateLaborDetailsPdfAndSendEmail(commonLoginId, recipientEmail);
	  }
}
