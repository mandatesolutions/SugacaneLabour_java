package com.sugarcanelabour.helper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CommonFunctions {

	
	@Value("${UPLOAD_PROFILEIMAGE}")
	private String UPLOAD_DIR;
//	 private static final String UPLOAD_DIR = "E:/New folder/Projects/filesUploadDir";  // Change to your desired location

	    public String saveLaborImage(MultipartFile image) throws Exception {
	        if (image != null && !image.isEmpty()) {
	            // Get the original file name
	            String fileName = System.currentTimeMillis() + "_" + image.getOriginalFilename();

	            // Define the path where the image will be saved
	            Path path = Paths.get(UPLOAD_DIR, fileName);

	            // Save the file
	            Files.copy(image.getInputStream(), path);

	            // Return the file URL (assuming you're using a relative path)
	            return "/uploads/" + fileName; // Adjust this URL based on how you serve static files
	        }
	        return null; // No image to save
	    }
}
