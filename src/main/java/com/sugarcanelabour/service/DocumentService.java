package com.sugarcanelabour.service;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import com.sugarcanelabour.helper.ApiResponse;
import com.sugarcanelabour.model.UploadDocumentDto;

public interface DocumentService {

//	String uploadFile(MultipartFile file, String roleName);

	

	//void uploadDocument(UploadDocumentDto uploadDocumentDto);


	ResponseEntity<ApiResponse<String>> handleFileUploadWithMetadata(UploadDocumentDto uploadDocumentDto,
			Long commonLoginId);




	

	

}
