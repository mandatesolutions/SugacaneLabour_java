package com.sugarcanelabour.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RegistrationDto {

	 @NotBlank(message = "Email cannot be blank")
	 @Email(message = "Invalid email format")
	 private String email;
	 
	 @NotBlank(message = "mobile no cannot be blank")
	 @Size(min = 10, max = 10, message = "Mobile number must be 10 digits")
	 private String mobileNo;
	 
	 @NotBlank(message = "password cannot be blank")
	 private String password;
	    
    @NotBlank(message = "First name cannot be blank")
    private String firstName;
    
    @NotBlank(message = "last name cannot be blank")
    private String lastName;

    private String gender;
    
    private String bloodGroup;
    
    @NotBlank(message = "Address cannot be blank")
    private String address;
	    
	    

}
