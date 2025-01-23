package com.sugarcanelabour.serviceimpl;

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
    public CommonLogin registerSuperAdmin(SuperAdminRegistrationDto superAdminDto) {
        if (superAdminDto.getPassword() == null || superAdminDto.getPassword().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        
        // Encrypt the password
        String encryptedPassword = passwordEncoder.encode(superAdminDto.getPassword());

        // Create a new Role for SUPER_ADMIN
        Role ROLE_ADMIN = new Role();
        ROLE_ADMIN.setRoleName("ADMIN"); // Set the role name

        // Create the CommonLogin object for the super admin
        CommonLogin AdminLogin = new CommonLogin();
        AdminLogin.setEmail(superAdminDto.getEmail());
        AdminLogin.setPassword(encryptedPassword);
        AdminLogin.setRole(ROLE_ADMIN);  // Associate the dynamically created role

        // Save the super admin login
        return loginRepository.save(AdminLogin);
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
