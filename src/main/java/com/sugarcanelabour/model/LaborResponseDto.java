package com.sugarcanelabour.model;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LaborResponseDto {
	 private Long id;
	    private String firstName;
	    private String lastName;
	    private String gender;
	    private String bloodGroup;
	    private String address;
	    private String districtId;
	    private String talukaId;
	    private List<UploadDocumentDto> documents;  // List of documents with types and links
}
