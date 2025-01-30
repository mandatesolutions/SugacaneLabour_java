package com.sugarcanelabour.serviceimpl;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
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
	
		// Prepare the response map
		if (role.equals("ROLE_SUP-ADMIN")|| role.equals("ROLE_ADMIN") || 
		        role.equals("ROLE_DCPO") || role.equals("ROLE_DEPUTY-COMMISSIONER")) {
			resp.setStatus(CommonMessages.SUCCESS);
			resp.setMessage(CommonMessages.CL_LOGIN_SUCCESSFUL);
			response.put("userId", user.get().getUserId());
			response.put("email", user.get().getEmail());
			response.put("role", role); // Include role in the response
			response.put("token", jwtToken);
			resp.setData(response);
			return new ResponseEntity<>(resp, HttpStatus.OK);
		} 
		// For Supervisor and Co-worker, add additional fields from RegistrationDto
		  else if (role.equals("ROLE_SUPERVISOR") || role.equals("ROLE_CO-WORKER")) {
		        // Fetch SupervisorDetails for Supervisor and Co-worker roles
		        Optional<SupervisorDetails> supervisorDetailsOptional = supervisorDetailsRepository.findByCommonLogin(user.get());
		        
		        if (!supervisorDetailsOptional.isPresent()) {
		            resp.setStatus(CommonMessages.FAILED);
		            resp.setMessage("Supervisor details not found.");
		            return new ResponseEntity<>(resp, HttpStatus.NOT_FOUND);
		        }

		        SupervisorDetails supervisorDetails = supervisorDetailsOptional.get();
		        
		        // Add fields from SupervisorDetails to response
		        response.put("userId", user.get().getUserId());
				response.put("email", user.get().getEmail());
				response.put("role", role); // Include role in the response
				response.put("token", jwtToken);
		        response.put("firstName", supervisorDetails.getFirstName());
		        response.put("lastName", supervisorDetails.getLastName());
		        response.put("gender", supervisorDetails.getGender());
		        response.put("bloodGroup", supervisorDetails.getBloodGroup());
		        response.put("address", supervisorDetails.getAddress());

		        resp.setStatus(CommonMessages.SUCCESS);
		        resp.setMessage(CommonMessages.CL_LOGIN_SUCCESSFUL);
		        resp.setData(response);
		        return new ResponseEntity<>(resp, HttpStatus.OK);
		    } else {
		        resp.setStatus(CommonMessages.FAILED);
		        resp.setMessage(CommonMessages.ROLE_INVALID);
		        return new ResponseEntity<>(resp, HttpStatus.BAD_REQUEST);
		    }
	
	
	
	}
	@Override
	public ResponseEntity<ApiResponse<Map<String, Object>>> registerSuperAdmin(
			SuperAdminRegistrationDto superAdminDto) {
		Map<String, Object> response = new HashMap<>();
		try {

			// Fetch the role by ID
			Optional<Role> roleOptional = roleRepository.findById(superAdminDto.getRoleId());
			if (roleOptional.isEmpty()) {
				response.put("status", "FAILED");
				response.put("message", "Role not found");
				return ResponseEntity.badRequest().body(response);
			}
			// Check if email already exists
			Optional<CommonLogin> existingUser = loginRepository.findByEmail(superAdminDto.getEmail());
			if (existingUser.isPresent()) {
				response.put("status", "FAILED");
				response.put("message", "Super Admin with this email already exists");
				return ResponseEntity.badRequest().body(response);
			}

			// Create and save the Super Admin
			CommonLogin superAdmin = new CommonLogin();
			superAdmin.setEmail(superAdminDto.getEmail());
			superAdmin.setPassword(passwordEncoder.encode(superAdminDto.getPassword()));
			superAdmin.setRole(roleOptional.get()); // Assign the role
			superAdmin.setStatus(UserStatus.ACTIVE);

			CommonLogin savedSuperAdmin = loginRepository.save(superAdmin);

			response.put("status", "SUCCESS");
			response.put("message", "Super Admin registered successfully");
			response.put("userId", savedSuperAdmin.getUserId());
			response.put("email", savedSuperAdmin.getEmail());
			response.put("role", savedSuperAdmin.getRole().getRoleName());

			return ResponseEntity.ok(response);
		} 
		catch (Exception e) {
			response.put("status", "FAILED");
			response.put("message", "Error while registering Super Admin: " + e.getMessage());
			return ResponseEntity.internalServerError().body(response);
		ApiResponse<Map<String, Object>> resp = new ApiResponse<>();
		// Fetch the role by ID
		Optional<Role> roleOptional = roleRepository.findById(superAdminDto.getRoleId());
		if (roleOptional.isEmpty()) {
			resp.setStatus(CommonMessages.FAILED);
			resp.setMessage(CommonMessages.ROLE_INVALID);
			return ResponseEntity.badRequest().body(resp);
		}
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
		superAdmin.setPassword(passwordEncoder.encode(superAdminDto.getPassword()));
		superAdmin.setRole(roleOptional.get()); // Assign the role
		superAdmin.setStatus(UserStatus.ACTIVE);

		CommonLogin savedSuperAdmin = loginRepository.save(superAdmin);

		resp.setStatus(CommonMessages.SUCCESS);
		resp.setMessage(CommonMessages.CL_REGISTER_SUCCESSFUL);
		response.put("userId", savedSuperAdmin.getUserId());
		response.put("email", savedSuperAdmin.getEmail());
		response.put("role", savedSuperAdmin.getRole().getRoleName());
		resp.setData(response);
		return ResponseEntity.ok(resp);

	}
	}
	// supervisor register

		@Override
		public ResponseEntity<Object> registerSupervisor(SupervisorDetails supervisorDetails) {
		    Map<String, Object> response = new HashMap<>();
		    try {
//		        // Admin can only register supervisors, check the role of the requester
//		        if (!supervisorDetails.getCommonLogin().getRole().getRoleName().equals("ROLE_ADMIN")) {
//		            log.warn("Unauthorized access attempt. Only admin can register supervisors.");
//		            response.put("status", "FAILED");
//		            response.put("message", "Only admin can register supervisors");
//		            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
//		        }

		        // Encrypt the password from SupervisorDetails entity
		        String encryptedPassword = passwordEncoder.encode(supervisorDetails.getCommonLogin().getPassword());
		        supervisorDetails.getCommonLogin().setPassword(encryptedPassword);

		        // Check if the supervisor email already exists
		        Optional<CommonLogin> existingSupervisor = loginRepository.findByEmail(supervisorDetails.getCommonLogin().getEmail());
		        if (existingSupervisor.isPresent()) {
		            response.put("status", "FAILED");
		            response.put("message", "Supervisor with this email already exists");
		            return ResponseEntity.badRequest().body(response);
		        }

		        // Create and save the new supervisor login (CommonLogin)
		        CommonLogin supervisorLogin = supervisorDetails.getCommonLogin();
		        supervisorLogin.setRole(roleRepository.findById(supervisorDetails.getCommonLogin().getRole().getId()).orElseThrow(() -> new IllegalArgumentException("Role not found")));
		        
		        CommonLogin savedSupervisorLogin = loginRepository.save(supervisorLogin);

		        // Link the saved CommonLogin back to the SupervisorDetails
		        supervisorDetails.setCommonLogin(savedSupervisorLogin); // Associate saved login with supervisor details

		        // Save SupervisorDetails
		        SupervisorDetails savedSupervisorDetails = supervisorDetailsRepository.save(supervisorDetails);

		        // Construct the response
		       
		        response.put("userId", savedSupervisorLogin.getUserId());
		        response.put("email", savedSupervisorLogin.getEmail());
		        response.put("role", savedSupervisorLogin.getRole().getRoleName());
		        response.put("firstName", savedSupervisorDetails.getFirstName());
		        response.put("lastName", savedSupervisorDetails.getLastName());
		        response.put("gender", savedSupervisorDetails.getGender());
		        response.put("bloodGroup", savedSupervisorDetails.getBloodGroup());
		        response.put("address", savedSupervisorDetails.getAddress());
		        response.put("district", savedSupervisorDetails.getDistrictId());
	        response.put("taluka", savedSupervisorDetails.getTalukaId());
		        response.put("status", "SUCCESS");
		        response.put("message", "Supervisor registered successfully");

		        return ResponseEntity.ok(response);
		    } catch (Exception e) {
		        response.put("status", "FAILED");
		        response.put("message", "Error while registering Supervisor: " + e.getMessage());
		        return ResponseEntity.internalServerError().body(response);
		    }
		}



	// register co-worker
//
//	@Override
//	public ResponseEntity<Object> registerCoworker(RegistrationDto coworkerDto) {
//		Map<String, Object> response = new HashMap<>();
//		try {
//			// Encrypt the password
//			String encryptedPassword = passwordEncoder.encode(coworkerDto.getPassword());
//
//			// Fetch the role from the DTO (assuming the role is passed via the DTO)
//			Optional<Role> roleOptional = roleRepository.findById(coworkerDto.getRoleId());
//			if (roleOptional.isEmpty()) {
//				response.put("status", "FAILED");
//				response.put("message", "Role not found");
//				return ResponseEntity.badRequest().body(response);
//			}
//
//			Role role = roleOptional.get();
//
//			// Check if the coworker's email already exists
//			Optional<CommonLogin> existingCoworker = loginRepository.findByEmail(coworkerDto.getEmail());
//			if (existingCoworker.isPresent()) {
//				response.put("status", "FAILED");
//				response.put("message", "Coworker with this email already exists");
//				return ResponseEntity.badRequest().body(response);
//			}
//
//			// Create and save the new coworker
//			CommonLogin coworker = new CommonLogin();
//			coworker.setEmail(coworkerDto.getEmail());
//			coworker.setMobileNo(coworkerDto.getMobileNo());
//			coworker.setPassword(encryptedPassword);
//			coworker.setRole(role); // Assign the role from DTO
//
//			CommonLogin savedCoworker = loginRepository.save(coworker);
//
//			// Add response fields
//			response.put("status", "SUCCESS");
//			response.put("message", "Coworker registered successfully");
//			response.put("userId", savedCoworker.getUserId());
//			response.put("email", savedCoworker.getEmail());
//			response.put("role", savedCoworker.getRole().getRoleName());
//			response.put("firstName", coworkerDto.getFirstName());
//			response.put("lastName", coworkerDto.getLastName());
//			response.put("gender", coworkerDto.getGender());
//			response.put("bloodGroup", coworkerDto.getBloodGroup());
//			response.put("address", coworkerDto.getAddress());
//
//			return ResponseEntity.ok(response);
//		} catch (Exception e) {
//			response.put("status", "FAILED");
//			response.put("message", "Error while registering Coworker: " + e.getMessage());
//			return ResponseEntity.internalServerError().body(response);
//		}
//	}
//
//	@Override
//	public ResponseEntity<Object> registerLaborer(RegistrationDto laborerDto) {
//		Map<String, Object> response = new HashMap<>();
//		try {
//			// Encrypt the password
//			String encryptedPassword = passwordEncoder.encode(laborerDto.getPassword());
//
//			// Fetch the laborer role from the DTO
//			Optional<Role> roleOptional = roleRepository.findById(laborerDto.getRoleId());
//			if (roleOptional.isEmpty()) {
//				response.put("status", "FAILED");
//				response.put("message", "Role not found");
//				return ResponseEntity.badRequest().body(response);
//			}
//
//			Role role = roleOptional.get();
//
//			// Check if the laborer's email already exists
//			Optional<CommonLogin> existingLaborer = loginRepository.findByEmail(laborerDto.getEmail());
//			if (existingLaborer.isPresent()) {
//				response.put("status", "FAILED");
//				response.put("message", "Laborer with this email already exists");
//				return ResponseEntity.badRequest().body(response);
//			}
//
//			// Create and save the new laborer
//			CommonLogin laborer = new CommonLogin();
//			laborer.setEmail(laborerDto.getEmail());
//			laborer.setMobileNo(laborerDto.getMobileNo());
//			laborer.setPassword(encryptedPassword);
//			laborer.setRole(role); // Assign role from DTO
//
//			CommonLogin savedLaborer = loginRepository.save(laborer);
//
//			// Add response fields
//			response.put("status", "SUCCESS");
//			response.put("message", "Laborer registered successfully");
//			response.put("userId", savedLaborer.getUserId());
//			response.put("email", savedLaborer.getEmail());
//			response.put("role", savedLaborer.getRole().getRoleName());
//			response.put("firstName", laborerDto.getFirstName());
//			response.put("lastName", laborerDto.getLastName());
//			response.put("gender", laborerDto.getGender());
//			response.put("bloodGroup", laborerDto.getBloodGroup());
//			response.put("address", laborerDto.getAddress());
//
//			return ResponseEntity.ok(response);
//		} catch (Exception e) {
//			response.put("status", "FAILED");
//			response.put("message", "Error while registering Laborer: " + e.getMessage());
//			return ResponseEntity.internalServerError().body(response);
//		}
//	}

}
