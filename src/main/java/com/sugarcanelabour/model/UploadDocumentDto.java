package com.sugarcanelabour.model;

import org.springframework.web.multipart.MultipartFile;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UploadDocumentDto {

	 private String documentType;  // To store the document type (e.g., ID Proof, Address Proof, etc.)
	    private MultipartFile file;   // The file to be uploaded

}
