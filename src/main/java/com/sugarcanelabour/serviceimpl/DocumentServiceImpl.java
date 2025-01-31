package com.sugarcanelabour.serviceimpl;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.sugarcanelabour.helper.ApiResponse;
import com.sugarcanelabour.model.UploadDocumentDto;
import com.sugarcanelabour.service.DocumentService;

import io.jsonwebtoken.io.IOException;
import lombok.extern.slf4j.Slf4j;
@Service
@Slf4j
public class DocumentServiceImpl implements DocumentService{
	
	 @Value("${file.upload.dir}")
	    private String uploadDir;

	
	@Override
	public ResponseEntity<ApiResponse<String>> handleFileUploadWithMetadata(UploadDocumentDto documentDto) {
	 if (documentDto.getFile() == null || documentDto.getFile().isEmpty()) {
         return ResponseEntity.badRequest().body(new ApiResponse<>("Error", "No file uploaded", null));
     }

     String documentType = documentDto.getDocumentType();
     MultipartFile file = documentDto.getFile();

     try {
         uploadFile(file, documentType); // Process the file along with the document type
     } catch (IOException e) {
         log.error("File upload failed", e);
         return ResponseEntity.internalServerError().body(new ApiResponse<>("Error", "File upload failed", null));
     }

     return ResponseEntity.ok(new ApiResponse<>("Success", "File uploaded successfully", null));
 }

 private void uploadFile(MultipartFile file, String documentType) throws IOException {
     // Get the original file name
     String fileName = file.getOriginalFilename();

     if (fileName == null) {
         throw new IOException("Invalid file name");
     }

     // Create the directory structure (optional, based on document type)
     Path filesUploadDir = Paths.get(uploadDir, documentType);
     Files.createDirectories(filesUploadDir); // Create directories if not exist

     // Create the full file path where the file will be saved
     Path path = Paths.get(filesUploadDir.toString(), fileName);

     // Save the file to the specified path
     file.transferTo("");

     log.info("Document uploaded: " + documentType + " - " + fileName);
 }
	
}
