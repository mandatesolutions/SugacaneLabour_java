package com.sugarcanelabour.model;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.sugarcanelabour.helper.Enums.DocumentType;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UploadDocumentDto {

	 
	 private List<DocumentType> documentTypes;  // List to store document types
	   private MultipartFile file;   // The file to be uploaded

}
