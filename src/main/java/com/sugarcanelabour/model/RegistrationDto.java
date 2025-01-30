package com.sugarcanelabour.model;

<<<<<<< HEAD
import com.sugarcanelabour.entity.CommonLogin;
import com.sugarcanelabour.entity.Role;

import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
=======
>>>>>>> 962f4c893e3c115ff02c6978a4457416930bd1d7
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RegistrationDto {

<<<<<<< HEAD
	 @NotBlank(message = "Email cannot be blank")
	 @Email(message = "Invalid email format")
	 private String email;
	 
	 @NotBlank(message = "mobile no cannot be blank")
	 @Size(min = 10, max = 10, message = "Mobile number must be 10 digits")
	 private String mobileNo;
	 
	 @NotBlank(message = "password cannot be blank")
	 private String password;
	    
    @NotBlank(message = "First name cannot be blank")
	public static String firstName;
    
    @NotBlank(message = "last name cannot be blank")
	public static String lastName;

    public static String gender;
    
    public static String bloodGroup;
    
    @NotBlank(message = "Address cannot be blank")
    public static String address;
	    
 
    private Long roleId;  
	    
    
    private long districtId;
    
    private long talukaId;
=======
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

	@NotBlank(message = "Role ID cannot be blank")
	private Long roleId;
>>>>>>> 962f4c893e3c115ff02c6978a4457416930bd1d7

	public static String firstName(CommonLogin commonLogin) {
		// TODO Auto-generated method stub
		return null;
	}

	public static String lastName(CommonLogin commonLogin) {
		// TODO Auto-generated method stub
		return null;
	}

	public static String gender(CommonLogin commonLogin) {
		// TODO Auto-generated method stub
		return null;
	}

	public static String address(CommonLogin commonLogin) {
		// TODO Auto-generated method stub
		return null;
	}

	public static String bloodGroup(CommonLogin commonLogin) {
		// TODO Auto-generated method stub
		return null;
	}

    //private RegistrationDto userDetails;
}
