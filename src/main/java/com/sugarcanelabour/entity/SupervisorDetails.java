package com.sugarcanelabour.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
public class SupervisorDetails {
	
	 @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;

	    private String firstName;
	    private String lastName;
	    private String gender;
	    private String bloodGroup;
	    private String address;

	    @OneToOne
	    @JoinColumn(name = "common_login_id")
	    private CommonLogin commonLogin;  // Link to CommonLogin for supervisor login details


}
