package com.sugarcanelabour.model;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SuperAdminRegistrationDto {
	
	
	 @Email(message = "Invalid email format")
	 private String email;
	

	 @NotEmpty(message = "Password cannot be null or empty")
	 private String password;

	 @NotNull(message = "Role cannot be null")
	    private Long roleId;
	 
}
