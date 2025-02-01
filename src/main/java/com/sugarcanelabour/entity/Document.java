package com.sugarcanelabour.entity;

import com.sugarcanelabour.helper.Enums.DocumentType;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
public class Document {
	  @Id
	  @GeneratedValue(strategy = GenerationType.IDENTITY)
	  private Long id;
	  

	  @Enumerated(EnumType.STRING)  // Ensure the enum is stored as a string in the database
	  private DocumentType documentType;
	 
	  private String fileName;
	  
	  private String filePath;
	  
	  private String documentLink; // Store the link or path to the uploaded document

	  
	  @ManyToOne
	  @JoinColumn(name = "common_login_id")
	  private CommonLogin commonLogin;

	  @ManyToOne
	    @JoinColumn(name = "supervisor_id")
	    private SupervisorDetails supervisorDetails;  
	  
}
