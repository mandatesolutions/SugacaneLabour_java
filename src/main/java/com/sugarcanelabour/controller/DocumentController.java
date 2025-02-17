package com.sugarcanelabour.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.sugarcanelabour.helper.ApiResponse;
import com.sugarcanelabour.helper.Enums.DocumentType;
import com.sugarcanelabour.model.UploadDocumentDto;
import com.sugarcanelabour.service.DocumentService;

import io.jsonwebtoken.io.IOException;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/sclm/coworker/document")
@Slf4j
public class DocumentController {

	private DocumentService documentService;

	public DocumentController(DocumentService documentService) {
		this.documentService = documentService;
	}

	@Operation(summary = "Upload document with document types", description = "This API is used to upload a document with its types.")
	@PostMapping("/upload")
	public ResponseEntity<ApiResponse<String>> uploadDocument(
			@RequestParam("documentTypes") List<DocumentType> documentTypes, // Accepting Enum List
			@RequestParam("file") MultipartFile file, @RequestParam("commonLoginId") Long commonLoginId) {

		// Create DTO and set values
		UploadDocumentDto uploadDocumentDto = new UploadDocumentDto();
		uploadDocumentDto.setDocumentTypes(documentTypes);
		uploadDocumentDto.setFile(file);

		return documentService.handleFileUploadWithMetadata(uploadDocumentDto, commonLoginId);
	}
	
	@GetMapping("/tests")
	ResponseEntity<Object> test() {
		log.info("***** Inside - Coworker Controller - test *****");
		Map<String, Object> response = new HashMap<>();
		response.put("status", "success");
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
}
