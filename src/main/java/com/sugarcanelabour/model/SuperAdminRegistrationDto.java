package com.sugarcanelabour.model;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
public class SuperAdminRegistrationDto {
	
	
	 @Email(message = "Invalid email format")
	 @NotBlank(message = "Email cannot be blank")
	 private String email;
	
	 @NotNull(message = "Password cannot be null")
	 @NotEmpty(message = "Password cannot be null or empty")
	 private String password;

}
