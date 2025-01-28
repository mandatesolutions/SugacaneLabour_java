package com.sugarcanelabour.model;

import com.sugarcanelabour.entity.Role;

import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LoginRequest {

	@NotBlank(message = "Email cannot be blank")
    @Email(message = "Invalid email format")
	private String email;
	
	@NotBlank(message = "Password cannot be blank")
    private String password;
	  
	 @ManyToOne(fetch = FetchType.LAZY)
	 @JoinColumn(name = "Roles")
	private Role role;
	 
}
