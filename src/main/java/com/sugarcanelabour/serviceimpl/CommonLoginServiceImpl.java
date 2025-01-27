package com.sugarcanelabour.serviceimpl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.sugarcanelabour.entity.CommonLogin;
import com.sugarcanelabour.entity.Role;
import com.sugarcanelabour.entity.SupervisorDetails;
import com.sugarcanelabour.model.RegistrationDto;
import com.sugarcanelabour.model.SuperAdminRegistrationDto;
import com.sugarcanelabour.repository.CommonLoginRepository;
import com.sugarcanelabour.repository.RoleRepository;
import com.sugarcanelabour.repository.SupervisorDetailsRepository;
import com.sugarcanelabour.service.CommonLoginService;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CommonLoginServiceImpl implements CommonLoginService {
	
	private CommonLoginRepository loginRepository;
    private SupervisorDetailsRepository supervisorDetailsRepository;
    private BCryptPasswordEncoder passwordEncoder;
    private RoleRepository roleRepository;
    
    public CommonLoginServiceImpl(CommonLoginRepository loginRepository,
                                  SupervisorDetailsRepository supervisorDetailsRepository,
                                  BCryptPasswordEncoder passwordEncoder,RoleRepository roleRepository) {
        this.loginRepository = loginRepository;
        this.supervisorDetailsRepository = supervisorDetailsRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository=roleRepository;
    }
    
    //login
    @Override
    public String login(String email, String password) {
        log.info("Attempting to login with email: {}", email);
        CommonLogin user = loginRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("Invalid email provided: {}", email);
                    return new IllegalArgumentException("Invalid email or password");
                });

        // Match the password using bcrypt encoder
        if (!passwordEncoder.matches(password, user.getPassword())) {
            log.warn("Password mismatch for email: {}", email);
            throw new IllegalArgumentException("Invalid email or password");
        }

        log.info("Login successful for role: {}", user.getRole());
        return "Login successful for role: " + user.getRole();
    }
    
    
    //super-admin register
    
    @Override
    @Transactional
    public ResponseEntity<Object> registerSuperAdmin(SuperAdminRegistrationDto superAdminDto) {
        Map<String, Object> response = new HashMap<>();

        try {
            // Validate password
            if (superAdminDto.getPassword() == null || superAdminDto.getPassword().isEmpty()) {
                response.put("status", "failure");
                response.put("message", "Password cannot be null or empty");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }

            // Encrypt the password
            String encryptedPassword = passwordEncoder.encode(superAdminDto.getPassword());

            // Retrieve or create the ADMIN role
            Role adminRole = roleRepository.findByRoleName("ADMIN").orElseGet(() -> {
                Role newRole = new Role();
                newRole.setRoleName("ADMIN");
                return roleRepository.save(newRole);
            });

            // Create the CommonLogin object for the super admin
            CommonLogin adminLogin = new CommonLogin();
            adminLogin.setEmail(superAdminDto.getEmail());
            adminLogin.setPassword(encryptedPassword);
            adminLogin.setRole(adminRole);

            // Save the super admin login
            CommonLogin savedAdmin = loginRepository.save(adminLogin);

            // Prepare response
            response.put("status", "success");
            response.put("message", "Super Admin registered successfully");
            response.put("data", savedAdmin);
            return new ResponseEntity<>(response, HttpStatus.CREATED);

        } catch (Exception e) {
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
