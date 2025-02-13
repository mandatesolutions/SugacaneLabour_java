package com.sugarcanelabour.serviceimpl;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.sugarcanelabour.entity.CommonLogin;
import com.sugarcanelabour.entity.Document;
import com.sugarcanelabour.helper.ApiResponse;
import com.sugarcanelabour.helper.Enums.DocumentType;
import com.sugarcanelabour.model.UploadDocumentDto;
import com.sugarcanelabour.repository.CommonLoginRepository;
import com.sugarcanelabour.repository.DocumentRepository;
import com.sugarcanelabour.service.DocumentService;

import io.jsonwebtoken.io.IOException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
@Service
@Slf4j
public class DocumentServiceImpl implements DocumentService{
	
	 @Value("${UPLOAD_DOCUMENTS}")
	    private String uploadDir;

	    private final DocumentRepository documentRepository;
	    private final CommonLoginRepository commonLoginRepository;

	    public DocumentServiceImpl(DocumentRepository documentRepository, CommonLoginRepository commonLoginRepository) {
	        this.documentRepository = documentRepository;
	        this.commonLoginRepository = commonLoginRepository;
	    }

	    @Override
	    @Transactional
	    public ResponseEntity<ApiResponse<String>> handleFileUploadWithMetadata(UploadDocumentDto uploadDocumentDto, Long commonLoginId) {
	        if (uploadDocumentDto.getFile() == null || uploadDocumentDto.getFile().isEmpty()) {
	            return ResponseEntity.badRequest().body(new ApiResponse<>("Error", "No file uploaded", null));
	        }

	        try {
	            String savedFilePath = uploadFile(uploadDocumentDto.getFile());
	            saveFileMetadata(uploadDocumentDto.getDocumentTypes(), savedFilePath, commonLoginId);
	            return ResponseEntity.ok(new ApiResponse<>("Success", "File uploaded successfully: " + savedFilePath, null));
	        } catch (IOException e) {
	            log.error("File upload failed", e);
	            return ResponseEntity.internalServerError().body(new ApiResponse<>("Error", "File upload failed", null));
	        }
	    }

	    private String uploadFile(MultipartFile file) throws IOException {
	        String fileName = file.getOriginalFilename();
	        if (fileName == null) {
	            throw new IOException("Invalid file name");
	        }

	        // Define directory structure for storing the file
	        Path filesUploadDir = Paths.get(uploadDir);
	        try {
				Files.createDirectories(filesUploadDir);
			} catch (java.io.IOException e) {
				e.printStackTrace();
			}

	        Path filePath = filesUploadDir.resolve(fileName);

	        // Save the file to the filesystem
	        try {
				file.transferTo(filePath.toFile());
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (java.io.IOException e) {
				e.printStackTrace();
			}

	        log.info("Document uploaded: {}", fileName);
	        return filePath.toString(); // Return the full file path
	    }

	    private void saveFileMetadata(List<DocumentType> documentTypes, String filePath, Long commonLoginId) {
	        CommonLogin commonLogin = commonLoginRepository.findById(commonLoginId)
	                .orElseThrow(() -> new RuntimeException("CommonLogin not found"));

	        for (DocumentType documentType : documentTypes) {
	            Document document = new Document();
	            document.setDocumentType(documentType);
	            document.setFilePath(filePath);
	            document.setCommonLogin(commonLogin);

	            // Generate the document link (assuming the file is accessible at some URL)
	            String documentLink = generateDocumentLink(filePath);
	            document.setDocumentLink(documentLink);

	            documentRepository.save(document);
	            log.info("Document metadata saved in DB: {}", filePath);
	        }
	    }

	    // Method to generate the document link (you can adjust this as per your requirements)
	    private String generateDocumentLink(String filePath) {
	        // Assuming the document is accessible via a URL based on the file path
	        // Replace this with your actual URL logic if needed (e.g., using a domain or cloud storage link)
	        String documentLink = "http://documents.com/files/" + new File(filePath).getName();
	        return documentLink;
	    }
	    
}
