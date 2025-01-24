package com.sugarcanelabour.serviceimpl;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;


import com.sugarcanelabour.entity.CommonLogin;
import com.sugarcanelabour.entity.Role;
import com.sugarcanelabour.entity.SupervisorDetails;
import com.sugarcanelabour.helper.JwtHelper;
import com.sugarcanelabour.model.LoginRequest;
import com.sugarcanelabour.model.RegistrationDto;
import com.sugarcanelabour.model.SuperAdminRegistrationDto;
import com.sugarcanelabour.repository.CommonLoginRepository;
import com.sugarcanelabour.repository.RoleRepository;
import com.sugarcanelabour.repository.SupervisorDetailsRepository;
import com.sugarcanelabour.service.CommonLoginService;
import com.sugarcanelabour.service.CustomJwtUserDetailService;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CommonLoginServiceImpl implements CommonLoginService {
	
	private CommonLoginRepository loginRepository;
    private SupervisorDetailsRepository supervisorDetailsRepository;
    private BCryptPasswordEncoder passwordEncoder;
    private RoleRepository roleRepository;
    private CustomJwtUserDetailService customJwtUserDetailService;
    private JwtHelper jwtHelper;
    
    public CommonLoginServiceImpl(CommonLoginRepository loginRepository,
                                  SupervisorDetailsRepository supervisorDetailsRepository,
                                  BCryptPasswordEncoder passwordEncoder,RoleRepository roleRepository,CustomJwtUserDetailService customJwtUserDetailService,JwtHelper jwtHelper) {
        this.loginRepository = loginRepository;
        this.supervisorDetailsRepository = supervisorDetailsRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository=roleRepository;
        this.customJwtUserDetailService=customJwtUserDetailService;
        this.jwtHelper=jwtHelper;
    }
    
    //login
    @Override
    public ResponseEntity<Object> login(LoginRequest request) {
        log.info("Attempting to login with email: {}", request.getEmail());

        // Response map to store response details
        Map<String, Object> response = new HashMap<>();

        // Find the user by email
        CommonLogin user = loginRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> {
                    log.warn("Invalid email provided: {}", request.getEmail());
                    response.put("status", "FAILED");
                    response.put("message", "Invalid email or password");
                    return new IllegalArgumentException("Invalid email or password");
                });

        // Validate the password
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            log.warn("Password mismatch for email: {}", request.getEmail());
            response.put("status", "FAILED");
            response.put("message", "Invalid email or password");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        // Populate the response details on successful login
        response.put("status", "SUCCESS");
        response.put("message", "Login successful");
        response.put("userId", user.getUserId());
        response.put("email", user.getEmail());
        response.put("role", user.getRole().getRoleName());

        log.info("Login successful for userId: {} with role: {}", user.getUserId(), user.getRole().getRoleName());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    
    
    //super-admin register
    
    @Transactional
    @Override
    public ResponseEntity<Object> registerSuperAdmin(SuperAdminRegistrationDto superAdminDto) {
        log.info("***** Inside - SuperAdminServiceImpl - registerSuperAdmin *****");
        
        Map<String, Object> response = new HashMap<>();

        try {
            // Debugging: Log the incoming values
            log.info("Received email: {}", superAdminDto.getEmail());
            log.info("Received password: {}", superAdminDto.getPassword()); // Log password to check for hidden characters

            // Validate email and password
            if (superAdminDto.getEmail() == null || superAdminDto.getEmail().isEmpty()) {
                response.put("status", "failure");
                response.put("message", "Email cannot be null or empty");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }

            if (superAdminDto.getPassword() == null || superAdminDto.getPassword().isEmpty()) {
                response.put("status", "failure");
                response.put("message", "Password cannot be null or empty");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }

            // Encrypt the password
            String encryptedPassword = passwordEncoder.encode(superAdminDto.getPassword());

            // Get or create the ADMIN role automatically, no need to pass it in the request
            Optional<Role> optionalRole = roleRepository.findByRoleName("ADMIN");
            Role ROLE_ADMIN = optionalRole.orElseGet(() -> {
                // Create the role if it doesn't exist
                Role newRole = new Role();
                newRole.setRoleName("ADMIN");
                return roleRepository.save(newRole);
            });

            // Create the CommonLogin object for the super admin
            CommonLogin adminLogin = new CommonLogin();
            adminLogin.setEmail(superAdminDto.getEmail()); // Set email first
            adminLogin.setPassword(encryptedPassword); // Then set password
            adminLogin.setRole(ROLE_ADMIN); // Automatically set the ADMIN role

            // Save the super admin login
            CommonLogin savedAdmin = loginRepository.save(adminLogin);

            // Prepare success response
            response.put("status", "success");
            response.put("message", "Super Admin registered successfully");
            response.put("data", savedAdmin); // Include the saved admin details

            return new ResponseEntity<>(response, HttpStatus.CREATED);

        } catch (Exception e) {
            // Handle exceptions and prepare failure response
            response.put("status", "failure");
            response.put("message", "Super Admin registration failed: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }






     // supervisor register
	  @Override
	    public CommonLogin registerSupervisor(RegistrationDto supervisorDto) {
	        // Create the CommonLogin for the supervisor
	        CommonLogin commonLogin = new CommonLogin();
	        commonLogin.setEmail(supervisorDto.getEmail());
	        commonLogin.setMobileNo(supervisorDto.getMobileNo());
	        commonLogin.setPassword(supervisorDto.getPassword());
	        

	        // Save the CommonLogin entity
	        CommonLogin savedCommonLogin = loginRepository.save(commonLogin);

	        // Create the SupervisorDetails entity
	        SupervisorDetails supervisorDetails = new SupervisorDetails();
	        supervisorDetails.setFirstName(supervisorDto.getFirstName());
	        supervisorDetails.setLastName(supervisorDto.getLastName());
	        supervisorDetails.setGender(supervisorDto.getGender());
	        supervisorDetails.setBloodGroup(supervisorDto.getBloodGroup());
	        supervisorDetails.setAddress(supervisorDto.getAddress());
	        supervisorDetails.setCommonLogin(savedCommonLogin);  // Link to the CommonLogin

	        // Save the SupervisorDetails entity
	        supervisorDetailsRepository.save(supervisorDetails);

	        return savedCommonLogin;
	    }
//	   @Override
//	    public CommonLogin registerSupervisor(RegistrationDto supervisorDto, String role) {
//	        // Admin can register supervisors
//	        if (!role.equals("ROLE_ADMIN")) {
//	            log.warn("Unauthorized access attempt. Only admin can register supervisors.");
//	            throw new IllegalArgumentException("Only admin can register supervisors");
//	        }
//
//	        // Create the CommonLogin for the supervisor
//	        CommonLogin commonLogin = new CommonLogin();
//	        commonLogin.setEmail(supervisorDto.getEmail());
//	        commonLogin.setMobileNo(supervisorDto.getMobileNo());
//	        
//	        // Encode the password before saving it
//	        String encodedPassword = passwordEncoder.encode(supervisorDto.getPassword());
//	        commonLogin.setPassword(encodedPassword);
//	        
//	        // Set the role for the supervisor
//	        commonLogin.setRole("ROLE_SUPERVISOR");
//
//	        // Save the CommonLogin entity
//	        CommonLogin savedCommonLogin = loginRepository.save(commonLogin);
//
//	        // Create the SupervisorDetails entity
//	        SupervisorDetails supervisorDetails = new SupervisorDetails();
//	        supervisorDetails.setFirstName(supervisorDto.getFirstName());
//	        supervisorDetails.setLastName(supervisorDto.getLastName());
//	        supervisorDetails.setGender(supervisorDto.getGender());
//	        supervisorDetails.setBloodGroup(supervisorDto.getBloodGroup());
//	        supervisorDetails.setAddress(supervisorDto.getAddress());
//	        supervisorDetails.setCommonLogin(savedCommonLogin);  // Link to the CommonLogin
//
//	        // Save the SupervisorDetails entity
//	        supervisorDetailsRepository.save(supervisorDetails);
//
//	        return savedCommonLogin;
//	    }
//	  
	  
}
