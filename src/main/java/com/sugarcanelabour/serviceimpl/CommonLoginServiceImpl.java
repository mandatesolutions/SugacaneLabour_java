package com.sugarcanelabour.serviceimpl;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private PasswordEncoder passwordEncoder;
    private RoleRepository roleRepository;
    private CustomJwtUserDetailService customJwtUserDetailService;
    private JwtHelper jwtHelper;

    public CommonLoginServiceImpl(CommonLoginRepository loginRepository,
                                  SupervisorDetailsRepository supervisorDetailsRepository,
                                  PasswordEncoder passwordEncoder, RoleRepository roleRepository,
                                  CustomJwtUserDetailService customJwtUserDetailService, JwtHelper jwtHelper) {
        this.loginRepository = loginRepository;
        this.supervisorDetailsRepository = supervisorDetailsRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.customJwtUserDetailService = customJwtUserDetailService;
        this.jwtHelper = jwtHelper;
    }

    // Login with JWT token generation
    @Override
    public ResponseEntity<Object> login(LoginRequest request) {
        log.info("Attempting to login with email: {}", request.getEmail());

        Map<String, Object> response = new HashMap<>();

        // Fetch user details based on email
        CommonLogin user = loginRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> {
                    log.warn("Invalid email provided: {}", request.getEmail());
                    response.put("status", "FAILED");
                    response.put("message", "Invalid email or password");
                    return new IllegalArgumentException("Invalid email or password");
                });

        // Check if password matches
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            log.warn("Password mismatch for email: {}", request.getEmail());
            response.put("status", "FAILED");
            response.put("message", "Invalid email or password");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        // Get the role of the user (for example, "Admin", "Supervisor")
        Role role = user.getRole();  // Fetch the role from the CommonLogin entity
        String roleName = (role != null) ? role.getRoleName() : "UNKNOWN"; // Extract the role name

        // Generate JWT Token
        String jwtToken = jwtHelper.generateToken(user, roleName);  // Pass the roleName as a string

        // Prepare the response map
        response.put("status", "SUCCESS");
        response.put("userId", user.getUserId());
        response.put("email", user.getEmail());
        response.put("role", roleName);  // Include role in the response

        // Dynamically set the login success message based on role
        String loginMessage = roleName + " login successful";
        response.put("message", loginMessage);  // "Admin login successful", "Supervisor login successful", etc.

        // Include the JWT token in the response
        response.put("token", jwtToken);

        log.info("Login successful for userId: {} with role: {}", user.getUserId(), roleName);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


//    @Transactional
//    @Override
//    public ResponseEntity<Object> registerSuperAdmin(SuperAdminRegistrationDto superAdminDto) {
//        log.info("Registering Super Admin with email: {}", superAdminDto.getEmail());
//
//        Map<String, Object> response = new HashMap<>();
//
//        // Validate email, password, and roleName
//        if (superAdminDto.getEmail() == null || superAdminDto.getEmail().isEmpty()) {
//            response.put("status", "FAILED");
//            response.put("message", "Email cannot be null or empty");
//            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
//        }
//
//        if (superAdminDto.getPassword() == null || superAdminDto.getPassword().isEmpty()) {
//            response.put("status", "FAILED");
//            response.put("message", "Password cannot be null or empty");
//            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
//        }
//
//        if (superAdminDto.getRoleName() == null || superAdminDto.getRoleName().isEmpty()) {
//            response.put("status", "FAILED");
//            response.put("message", "Role name cannot be null or empty");
//            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
//        }
//
//        try {
//            // Encrypt the password
//            String encryptedPassword = passwordEncoder.encode(superAdminDto.getPassword());
//
//            // Fetch role by roleName
//            Optional<Role> roleOptional = roleRepository.findByRoleName(superAdminDto.getRoleName());
//            if (roleOptional.isEmpty()) {
//                response.put("status", "FAILED");
//                response.put("message", "Role '" + superAdminDto.getRoleName() + "' does not exist");
//                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
//            }
//
//            Role role = roleOptional.get();
//
//            // Check if a user with the same email already exists
//            Optional<CommonLogin> existingUser = loginRepository.findByEmail(superAdminDto.getEmail());
//            if (existingUser.isPresent()) {
//                log.warn("Super Admin with email {} already exists", superAdminDto.getEmail());
//                response.put("status", "FAILED");
//                response.put("message", "Super Admin with this email already exists");
//                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
//            }
//
//            // Create and save the super admin user
//            CommonLogin superAdmin = new CommonLogin();
//            superAdmin.setEmail(superAdminDto.getEmail());
//            superAdmin.setPassword(encryptedPassword);
//            superAdmin.setRole(role); // Map the role entity
//
//            CommonLogin savedSuperAdmin = loginRepository.save(superAdmin);
//
//            response.put("status", "SUCCESS");
//            response.put("message", "Super Admin registered successfully");
//            response.put("userId", savedSuperAdmin.getUserId());
//            response.put("email", savedSuperAdmin.getEmail());
//            response.put("role", savedSuperAdmin.getRole().getRoleName());
//
//            log.info("Super Admin registered successfully with email: {}", superAdminDto.getEmail());
//            return new ResponseEntity<>(response, HttpStatus.CREATED);
//
//        } catch (Exception e) {
//            log.error("Error while registering Super Admin: {}", e.getMessage());
//            response.put("status", "FAILED");
//            response.put("message", "Super Admin registration failed: " + e.getMessage());
//            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }








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
