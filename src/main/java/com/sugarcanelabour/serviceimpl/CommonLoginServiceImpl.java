package com.sugarcanelabour.serviceimpl;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.sugarcanelabour.entity.CommonLogin;
import com.sugarcanelabour.entity.Role;
import com.sugarcanelabour.entity.SupervisorDetails;
import com.sugarcanelabour.helper.ApiResponse;
import com.sugarcanelabour.helper.CommonMessages;
import com.sugarcanelabour.helper.Enums.UserStatus;
import com.sugarcanelabour.helper.JwtHelper;
import com.sugarcanelabour.model.LoginRequest;
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

	private PasswordEncoder passwordEncoder;
	private RoleRepository roleRepository;
	private JwtHelper jwtHelper;

	private SupervisorDetailsRepository supervisorDetailsRepository;

	public CommonLoginServiceImpl(CommonLoginRepository loginRepository, PasswordEncoder passwordEncoder,
			RoleRepository roleRepository, JwtHelper jwtHelper,SupervisorDetailsRepository supervisorDetailsRepository) {
		this.loginRepository = loginRepository;
		this.passwordEncoder = passwordEncoder;
		this.roleRepository = roleRepository;
		this.jwtHelper = jwtHelper;
		this.supervisorDetailsRepository=supervisorDetailsRepository;
	}

	// Login with JWT token generation
	//@Cacheable(value = "loginCache", key = "#request.email")
	@Override
	public ResponseEntity<ApiResponse<Map<String, Object>>> login(LoginRequest request) {
		if (log.isInfoEnabled()) {
			log.info("***** Inside CommonLoginServiceImpl - login *****");
		}
		Map<String, Object> response = new HashMap<>();
		ApiResponse<Map<String, Object>> resp = new ApiResponse<>();
		// Fetch user details based on email
		Optional<CommonLogin> user = loginRepository.findByEmail(request.getEmail());
		if (!user.isPresent()) {
			resp.setStatus(CommonMessages.FAILED);
			resp.setMessage(CommonMessages.CL_EMAIL_NF);
			return new ResponseEntity<>(resp, HttpStatus.NOT_FOUND);
		}
		String role = user.get().getRole().getRoleName();
		// Check if password matches
		if (!passwordEncoder.matches(request.getPassword(), user.get().getPassword())) {
			resp.setStatus(CommonMessages.FAILED);
			resp.setMessage(CommonMessages.CL_PASSWORD_NV);
			return new ResponseEntity<>(resp, HttpStatus.BAD_REQUEST);
		}
	

		// Generate JWT Token
		String jwtToken = jwtHelper.generateToken(user.get());
	
		// Convert role to user-friendly format
		String formattedRole = role.replace("ROLE_", "").replace("-", " ").toUpperCase();
		
		// Prepare the response map
		resp.setStatus(CommonMessages.SUCCESS);
		
		// Prepare the response map
		if (role.equals("ROLE_SUP-ADMIN")|| role.equals("ROLE_ADMIN") || 
		        role.equals("ROLE_DCPO") || role.equals("ROLE_DEPUTY-COMMISSIONER")) {
			resp.setStatus(CommonMessages.SUCCESS);
			  resp.setMessage(String.format("%s login successfully", formattedRole));
			response.put("userId", user.get().getUserId());
			response.put("email", user.get().getEmail());
			response.put("role", role); // Include role in the response
			response.put("token", jwtToken);
			resp.setData(response);
			return new ResponseEntity<>(resp, HttpStatus.OK);
		} 
		
		// For Supervisor and Co-worker, add additional fields from RegistrationDto
		if (role.equals("ROLE_SUPERVISOR") || role.equals("ROLE_CO-WORKER")) {
		    Optional<SupervisorDetails> supervisorDetailsOptional = supervisorDetailsRepository.findByCommonLogin(user.get());

		    if (!supervisorDetailsOptional.isPresent()) {
		        resp.setStatus(CommonMessages.FAILED);
		        resp.setMessage("Supervisor details not found.");
		        return new ResponseEntity<>(resp, HttpStatus.NOT_FOUND);
		    }

		    SupervisorDetails supervisorDetails = supervisorDetailsOptional.get();

		    response.put("userId", user.get().getUserId());
		    response.put("email", user.get().getEmail());
		    response.put("role", role);
		    response.put("token", jwtToken);
		    response.put("firstName", supervisorDetails.getFirstName());
		    response.put("lastName", supervisorDetails.getLastName());
		    response.put("gender", supervisorDetails.getGender());
		    response.put("bloodGroup", supervisorDetails.getBloodGroup());
		    response.put("address", supervisorDetails.getAddress());
		    response.put("districtId", supervisorDetails.getDistrictId());
		    response.put("talukaId", supervisorDetails.getTalukaId());

		    // **Ensure the ApiResponse is properly set**
		    resp.setStatus(CommonMessages.SUCCESS);
		    resp.setMessage(String.format("%s login successfully", formattedRole));
		    resp.setData(response);
		}

		// Ensure the final response contains all necessary fields
		return new ResponseEntity<>(resp, HttpStatus.OK);
	}
	
	//SUPER-ADMIN REGISTER
	@Override
	public ResponseEntity<ApiResponse<Map<String, Object>>> registerSuperAdmin(SuperAdminRegistrationDto superAdminDto) {
	    ApiResponse<Map<String, Object>> resp = new ApiResponse<>();
	    Map<String, Object> response = new HashMap<>();

	    try {
	        // Fetch the role by ID
	        Optional<Role> roleOptional = roleRepository.findById(superAdminDto.getRoleId());
	        if (roleOptional.isEmpty()) {
	            resp.setStatus(CommonMessages.FAILED);
	            resp.setMessage(CommonMessages.ROLE_INVALID);
	            return ResponseEntity.badRequest().body(resp);
	        }

	        // Encrypt the password before saving
	        String encryptedPassword = passwordEncoder.encode(superAdminDto.getPassword());

	        // Check if email already exists
	        Optional<CommonLogin> existingUser = loginRepository.findByEmail(superAdminDto.getEmail());
	        if (existingUser.isPresent()) {
	            resp.setStatus(CommonMessages.FAILED);
	            resp.setMessage(CommonMessages.CL_EMAIL_AE);
	            return ResponseEntity.badRequest().body(resp);
	        }

	        // Create and save the Super Admin
	        CommonLogin superAdmin = new CommonLogin();
	        superAdmin.setEmail(superAdminDto.getEmail());
	        superAdmin.setPassword(encryptedPassword);  // Set encrypted password
	        superAdmin.setRole(roleOptional.get());    // Assign the role
	        superAdmin.setStatus(UserStatus.ACTIVE);    // Set user status (ACTIVE)

	        CommonLogin savedSuperAdmin = loginRepository.save(superAdmin);

	        // Prepare response
	        response.put("userId", savedSuperAdmin.getUserId());
	        response.put("email", savedSuperAdmin.getEmail());
	        response.put("role", savedSuperAdmin.getRole().getRoleName());

	        resp.setStatus(CommonMessages.SUCCESS);
	        resp.setMessage(CommonMessages.CL_REGISTER_SUCCESSFUL);
	        resp.setData(response);

	        return ResponseEntity.ok(resp);
	    } catch (Exception e) {
	        resp.setStatus(CommonMessages.FAILED);
	        resp.setMessage("Error while registering Super Admin: " + e.getMessage());
	        return ResponseEntity.internalServerError().body(resp);
	    }
	}


	
	// supervisor register
	@Override
	public ResponseEntity<ApiResponse<Map<String, Object>>> registerSupervisor(RegistrationDto registrationDto) {
	    ApiResponse<Map<String, Object>> resp = new ApiResponse<>();
	    Map<String, Object> response = new HashMap<>();

	    try {
	        // Fetch the role by ID
	        Optional<Role> roleOptional = roleRepository.findById(registrationDto.getRoleId());
	        if (roleOptional.isEmpty()) {
	            resp.setStatus("FAILED");
	            resp.setMessage("Invalid role ID.");
	            return ResponseEntity.badRequest().body(resp);
	        }

	        // Check if email already exists
	        Optional<CommonLogin> existingUser = loginRepository.findByEmail(registrationDto.getEmail());
	        if (existingUser.isPresent()) {
	            resp.setStatus("FAILED");
	            resp.setMessage("Email already exists.");
	            return ResponseEntity.badRequest().body(resp);
	        }

	        // Encrypt the password
	        String encryptedPassword = passwordEncoder.encode(registrationDto.getPassword());

	        // 1. Create the CommonLogin entity
	        CommonLogin commonLogin = new CommonLogin();
	        commonLogin.setEmail(registrationDto.getEmail());
	        commonLogin.setMobileNo(registrationDto.getMobileNo());
	        commonLogin.setPassword(encryptedPassword); // Set encrypted password

	        // Assign the role to the CommonLogin entity
	        commonLogin.setRole(roleOptional.get());
	        loginRepository.save(commonLogin);

	        // 2. Create the SupervisorDetails entity
	        SupervisorDetails supervisorDetails = new SupervisorDetails();
	        supervisorDetails.setFirstName(registrationDto.getFirstName());
	        supervisorDetails.setLastName(registrationDto.getLastName());
	        supervisorDetails.setGender(registrationDto.getGender());
	        supervisorDetails.setBloodGroup(registrationDto.getBloodGroup());
	        supervisorDetails.setAddress(registrationDto.getAddress());
	        supervisorDetails.setDistrictId(String.valueOf(registrationDto.getDistrictId()));
	        supervisorDetails.setTalukaId(String.valueOf(registrationDto.getTalukaId()));
	        supervisorDetails.setCommonLogin(commonLogin); // Link CommonLogin to SupervisorDetails
	        supervisorDetailsRepository.save(supervisorDetails);

	        // Prepare response
	        response.put("userId", commonLogin.getUserId());
	        response.put("email", commonLogin.getEmail());
	        response.put("role", commonLogin.getRole().getRoleName());
	        response.put("supervisorId", supervisorDetails.getId());
	        response.put("districtId", supervisorDetails.getDistrictId());
	        response.put("talukaId", supervisorDetails.getTalukaId());

	        resp.setStatus("SUCCESS");
	        resp.setMessage("Supervisor registered successfully.");
	        resp.setData(response);

	        return ResponseEntity.ok(resp);
	    } catch (Exception e) {
	        resp.setStatus("FAILED");
	        resp.setMessage("Error while registering supervisor: " + e.getMessage());
	        return ResponseEntity.internalServerError().body(resp);
	    }
	}






	// register co-worker

	@Override
	public ResponseEntity<ApiResponse<Map<String, Object>>> registerCoworker(RegistrationDto coworkerDto) {
	    ApiResponse<Map<String, Object>> resp = new ApiResponse<>();
	    Map<String, Object> response = new HashMap<>();

	    try {
	        // Encrypt the password
	        String encryptedPassword = passwordEncoder.encode(coworkerDto.getPassword());

	        // Fetch the role from the DTO (assuming the role is passed via the DTO)
	        Optional<Role> roleOptional = roleRepository.findById(coworkerDto.getRoleId());
	        if (roleOptional.isEmpty()) {
	            resp.setStatus("FAILED");
	            resp.setMessage("Role not found");
	            return ResponseEntity.badRequest().body(resp);
	        }
	        
	        

	        Role role = roleOptional.get();

	        // Check if the coworker's email already exists
	        Optional<CommonLogin> existingCoworker = loginRepository.findByEmail(coworkerDto.getEmail());
	        if (existingCoworker.isPresent()) {
	            resp.setStatus("FAILED");
	            resp.setMessage("Coworker with this email already exists");
	            return ResponseEntity.badRequest().body(resp);
	        }

	        // Create and save the new coworker
	        CommonLogin coworker = new CommonLogin();
	        coworker.setEmail(coworkerDto.getEmail());
	        coworker.setMobileNo(coworkerDto.getMobileNo());
	        coworker.setPassword(encryptedPassword);
	        coworker.setRole(role); // Assign the role from DTO

	        CommonLogin savedCoworker = loginRepository.save(coworker);

	        // Add response fields
	        response.put("userId", savedCoworker.getUserId());
	        response.put("email", savedCoworker.getEmail());
	        response.put("role", savedCoworker.getRole().getRoleName());
	        response.put("firstName", coworkerDto.getFirstName());
	        response.put("lastName", coworkerDto.getLastName());
	        response.put("gender", coworkerDto.getGender());
	        response.put("bloodGroup", coworkerDto.getBloodGroup());
	        response.put("address", coworkerDto.getAddress());

	        resp.setStatus("SUCCESS");
	        resp.setMessage("CO-WORKER registered successfully.");
	        resp.setData(response);

	        return ResponseEntity.ok(resp);
	    } catch (Exception e) {
	        resp.setStatus("FAILED");
	        resp.setMessage("Error while registering Coworker: " + e.getMessage());
	        return ResponseEntity.internalServerError().body(resp);
	    }
	}


	@Override
	public ResponseEntity<ApiResponse<Map<String, Object>>> registerLaborer(RegistrationDto laborerDto) {
	    ApiResponse<Map<String, Object>> resp = new ApiResponse<>();
	    Map<String, Object> response = new HashMap<>();

	    try {
	        // Encrypt the password
	        String encryptedPassword = passwordEncoder.encode(laborerDto.getPassword());

	        // Fetch the laborer role from the DTO
	        Optional<Role> roleOptional = roleRepository.findById(laborerDto.getRoleId());
	        if (roleOptional.isEmpty()) {
	            resp.setStatus("FAILED");
	            resp.setMessage("Role not found");
	            return ResponseEntity.badRequest().body(resp);
	        }

	        Role role = roleOptional.get();

	        // Check if the laborer's email already exists
	        Optional<CommonLogin> existingLaborer = loginRepository.findByEmail(laborerDto.getEmail());
	        if (existingLaborer.isPresent()) {
	            resp.setStatus("FAILED");
	            resp.setMessage("Laborer with this email already exists");
	            return ResponseEntity.badRequest().body(resp);
	        }

	        // Create and save the new laborer
	        CommonLogin laborer = new CommonLogin();
	        laborer.setEmail(laborerDto.getEmail());
	        laborer.setMobileNo(laborerDto.getMobileNo());
	        laborer.setPassword(encryptedPassword);
	        laborer.setRole(role); // Assign role from DTO

	        CommonLogin savedLaborer = loginRepository.save(laborer);

	        // Add response fields
	        response.put("userId", savedLaborer.getUserId());
	        response.put("email", savedLaborer.getEmail());
	        response.put("role", savedLaborer.getRole().getRoleName());
	        response.put("firstName", laborerDto.getFirstName());
	        response.put("lastName", laborerDto.getLastName());
	        response.put("gender", laborerDto.getGender());
	        response.put("bloodGroup", laborerDto.getBloodGroup());
	        response.put("address", laborerDto.getAddress());

	        resp.setStatus("SUCCESS");
	        resp.setMessage("Laborer registered successfully.");
	        resp.setData(response);

	        return ResponseEntity.ok(resp);
	    } catch (Exception e) {
	        resp.setStatus("FAILED");
	        resp.setMessage("Error while registering Laborer: " + e.getMessage());
	        return ResponseEntity.internalServerError().body(resp);
	    }
	}
}
