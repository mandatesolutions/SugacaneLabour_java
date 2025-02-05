package com.sugarcanelabour.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RegistrationDto {

	private String firstName;
	
	private String lastName;
	
	private String gender;
	
	private String bloodGroup;
	
	private String address;
	
	 @NotBlank(message = "Email cannot be blank")
	 @Email(message = "Invalid email format")
	 private String email;
	 
	 @NotBlank(message = "mobile no cannot be blank")
	 @Size(min = 10, max = 10, message = "Mobile number must be 10 digits")
	 private String mobileNo;
	 
	 @NotBlank(message = "password cannot be blank")
	 private String password;
	    
  
 
    private Long roleId;  
	    
    
    private long districtId;
    
    private long talukaId;
    
	private Long age;
	
    private Long familyMembers;

    private String medicalHistory;
	   

}
