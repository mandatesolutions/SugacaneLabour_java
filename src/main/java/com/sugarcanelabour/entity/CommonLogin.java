package com.sugarcanelabour.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
@Table(name = "common_login")
public class CommonLogin {
	
		@Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long userId;

		
	    private String email;
	    
	   
	    private String mobileNo;
	    
	   
	    private String password;
	  
	    
//	    @OneToMany(mappedBy = "commonLogin", cascade = CascadeType.ALL, orphanRemoval = true)
//	    private List<Document> documents;

	    @JsonIgnore
	    @ManyToOne(fetch = FetchType.LAZY)
	    @JoinColumn(name = "Roles")
	    private Role role; 
	  

}
