package com.sugarcanelabour.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.sugarcanelabour.helper.ApiResponse;
import com.sugarcanelabour.model.UploadDocumentDto;
import com.sugarcanelabour.service.DocumentService;

import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/sclm/document")
@Slf4j
public class DocumentController {
	
	private DocumentService documentService;

	public DocumentController(DocumentService documentService) {
		this.documentService = documentService;
	}
	
	  @Operation(summary = "Upload document with document type", description = "This API is used to upload a document with its type")
	    @PostMapping("/upload")
	    public ResponseEntity<ApiResponse<String>> uploadDocument(@RequestParam("document") UploadDocumentDto documentDto) {
	        return documentService.handleFileUploadWithMetadata(documentDto);
	    }
	

}
