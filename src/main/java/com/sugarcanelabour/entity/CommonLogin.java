package com.sugarcanelabour.entity;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
@Table
public class CommonLogin {
	
	  @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long userId;

	    private String email;
	    private String mobileNo;
	    private String password;
	    private String role;
	    
//	    @OneToMany(mappedBy = "commonLogin", cascade = CascadeType.ALL, orphanRemoval = true)
//	    private List<Document> documents;


}
