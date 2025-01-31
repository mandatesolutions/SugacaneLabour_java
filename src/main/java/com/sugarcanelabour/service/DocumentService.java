package com.sugarcanelabour.service;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import com.sugarcanelabour.helper.ApiResponse;
import com.sugarcanelabour.model.UploadDocumentDto;

public interface DocumentService {

	
	ResponseEntity<ApiResponse<String>> handleFileUploadWithMetadata(UploadDocumentDto documentDto);

}
