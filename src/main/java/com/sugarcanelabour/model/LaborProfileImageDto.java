package com.sugarcanelabour.model;

import org.springframework.web.multipart.MultipartFile;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter

public class LaborProfileImageDto {
	
	 private MultipartFile profileImage;
}
