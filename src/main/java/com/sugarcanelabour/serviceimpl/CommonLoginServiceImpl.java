package com.sugarcanelabour.serviceimpl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.sugarcanelabour.entity.CommonLogin;
import com.sugarcanelabour.entity.Document;
import com.sugarcanelabour.entity.Role;
import com.sugarcanelabour.entity.SupervisorDetails;
import com.sugarcanelabour.entity.Taluka;
import com.sugarcanelabour.exception.ResourceNotFoundException;
import com.sugarcanelabour.helper.ApiResponse;
import com.sugarcanelabour.helper.CommonFunctions;
import com.sugarcanelabour.helper.CommonMessages;
import com.sugarcanelabour.helper.Enums.UserStatus;
import com.sugarcanelabour.helper.JwtHelper;
import com.sugarcanelabour.model.LoginRequest;
import com.sugarcanelabour.model.RegistrationDto;
import com.sugarcanelabour.model.SuperAdminRegistrationDto;
import com.sugarcanelabour.repository.CommonLoginRepository;
import com.sugarcanelabour.repository.DocumentRepository;
import com.sugarcanelabour.repository.RoleRepository;
import com.sugarcanelabour.repository.SupervisorDetailsRepository;
import com.sugarcanelabour.repository.TalukaRepository;
import com.sugarcanelabour.service.CommonLoginService;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CommonLoginServiceImpl implements CommonLoginService {

	private CommonLoginRepository loginRepository;

	private PasswordEncoder passwordEncoder;
	private RoleRepository roleRepository;
	private JwtHelper jwtHelper;
	private RedisTemplate<String, Object> redisTemplate;
	private CommonFunctions commonFunctions;
	private DocumentRepository documentRepository;
	private TalukaRepository talukaRepo;

	private SupervisorDetailsRepository supervisorDetailsRepository;

	public CommonLoginServiceImpl(CommonLoginRepository loginRepository, PasswordEncoder passwordEncoder,
			RoleRepository roleRepository, JwtHelper jwtHelper, RedisTemplate<String, Object> redisTemplate,
			CommonFunctions commonFunctions, DocumentRepository documentRepository, TalukaRepository talukaRepo,
			SupervisorDetailsRepository supervisorDetailsRepository) {
		super();

		this.loginRepository = loginRepository;
		this.passwordEncoder = passwordEncoder;
		this.roleRepository = roleRepository;
		this.jwtHelper = jwtHelper;
		this.redisTemplate = redisTemplate;
		this.commonFunctions = commonFunctions;
		this.documentRepository = documentRepository;
		this.talukaRepo = talukaRepo;
		this.supervisorDetailsRepository = supervisorDetailsRepository;
	}

	// @Cacheable(value = "loginCache", key = "#request.email")
	@Override
	@Transactional
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


		// Check if the user is active
		if (UserStatus.IN_ACTIVE.equals(user.get().getStatus())) {
			resp.setStatus(CommonMessages.FAILED);
			resp.setMessage("User is not active.");
			return new ResponseEntity<>(resp, HttpStatus.FORBIDDEN);
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
		if (role.equals("ROLE_SUP-ADMIN") || role.equals("ROLE_ADMIN") || role.equals("ROLE_DCPO")
				|| role.equals("ROLE_DEPUTY-COMMISSIONER")) {
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
		if (role.equals("ROLE_SUPERVISOR") || role.equals("ROLE_COWORKER")) {
			Optional<SupervisorDetails> supervisorDetailsOptional = supervisorDetailsRepository
					.findByCommonLogin(user.get());

			if (!supervisorDetailsOptional.isPresent()) {
				resp.setStatus(CommonMessages.FAILED);
				resp.setMessage("User details not found.");
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
			response.put("districtId", supervisorDetails.getTaluka().getDistrict().getDistrictId());
			response.put("talukaId", supervisorDetails.getTaluka().getTalukaId());

			// **Ensure the ApiResponse is properly set**
			resp.setStatus(CommonMessages.SUCCESS);
			resp.setMessage(String.format("%s login successfully", formattedRole));
			resp.setData(response);
		}

		// For Labor role, add UUID and other fields from SupervisorDetails
		if (role.equals("ROLE_LABOR")) {
			// Ensure UUID is added only for Labor
			Optional<SupervisorDetails> supervisorDetailsOptional = supervisorDetailsRepository
					.findByCommonLogin(user.get());

			if (!supervisorDetailsOptional.isPresent()) {
				resp.setStatus(CommonMessages.FAILED);
				resp.setMessage("Labor details not found.");
				return new ResponseEntity<>(resp, HttpStatus.NOT_FOUND);
			}

			SupervisorDetails supervisorDetails = supervisorDetailsOptional.get();

			response.put("userId", user.get().getUserId());
			response.put("email", user.get().getEmail());
			response.put("role", role);
			response.put("token", jwtToken);
			response.put("uuid", user.get().getUuid()); // Add uuid for Labor role
			response.put("firstName", supervisorDetails.getFirstName());
			response.put("lastName", supervisorDetails.getLastName());
			response.put("gender", supervisorDetails.getGender());
			response.put("bloodGroup", supervisorDetails.getBloodGroup());
			response.put("address", supervisorDetails.getAddress());
			response.put("districtId", supervisorDetails.getTaluka().getDistrict().getDistrictId());
			response.put("talukaId", supervisorDetails.getTaluka().getTalukaId());

			resp.setStatus(CommonMessages.SUCCESS);
			resp.setMessage(String.format("%s login successfully", formattedRole));
			resp.setData(response);
		}

		return new ResponseEntity<>(resp, HttpStatus.OK);
	}

	// SUPER-ADMIN REGISTER
	@Override
	@Transactional
	public ResponseEntity<ApiResponse<Map<String, Object>>> registerSuperAdmin(
			SuperAdminRegistrationDto superAdminDto) {
		ApiResponse<Map<String, Object>> resp = new ApiResponse<>();
		Map<String, Object> response = new HashMap<>();

		Taluka talukaDetails = talukaRepo.findById(superAdminDto.getTalukaId()).orElseThrow(
				() -> new ResourceNotFoundException("Taluka with the Id" + superAdminDto.getTalukaId() + " not found"));

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

			CommonLogin superAdmin = new CommonLogin();
			superAdmin.setEmail(superAdminDto.getEmail());
			superAdmin.setPassword(encryptedPassword); // Set encrypted password
			superAdmin.setRole(roleOptional.get()); // Assign the role
			superAdmin.setStatus(UserStatus.ACTIVE); // Set user status (ACTIVE)

			CommonLogin savedSuperAdmin = loginRepository.save(superAdmin);

			SupervisorDetails supervisorDetails = new SupervisorDetails();
			supervisorDetails.setFirstName(superAdminDto.getFirstName());
			supervisorDetails.setLastName(superAdminDto.getLastName());
			supervisorDetails.setTaluka(talukaDetails);
			supervisorDetails.setCommonLogin(savedSuperAdmin);

			supervisorDetailsRepository.save(supervisorDetails);

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

	@Override
	@Transactional
	public ResponseEntity<ApiResponse<Map<String, Object>>> registerSupervisor(RegistrationDto registrationDto) {
		ApiResponse<Map<String, Object>> resp = new ApiResponse<>();
		Map<String, Object> response = new HashMap<>();
		Taluka talukaDetails = talukaRepo.findById(registrationDto.getTalukaId())
				.orElseThrow(() -> new ResourceNotFoundException(
						"Taluka with the Id" + registrationDto.getTalukaId() + " not found"));
		try {
			// Fetch Supervisor Role
			Optional<Role> roleOptional = roleRepository.findByRoleName("ROLE_SUPERVISOR");
			if (roleOptional.isEmpty()) {
				resp.setStatus("FAILED");
				resp.setMessage("Supervisor role not found.");
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

			// Create CommonLogin entity
			CommonLogin commonLogin = new CommonLogin();
			commonLogin.setEmail(registrationDto.getEmail());
			commonLogin.setMobileNo(registrationDto.getMobileNo());
			commonLogin.setPassword(encryptedPassword);
			commonLogin.setRole(roleOptional.get()); // Assign Supervisor role

			loginRepository.save(commonLogin);

			// Fetch the logged-in user as `registeredBy`**
			String loggedInEmail = SecurityContextHolder.getContext().getAuthentication().getName();
			CommonLogin registeredByUser = loginRepository.findByEmail(loggedInEmail)
					.orElseThrow(() -> new RuntimeException("Logged-in user not found"));

			// Create SupervisorDetails entity
			SupervisorDetails supervisorDetails = new SupervisorDetails();
			supervisorDetails.setFirstName(registrationDto.getFirstName());
			supervisorDetails.setLastName(registrationDto.getLastName());
			supervisorDetails.setGender(registrationDto.getGender());
			supervisorDetails.setBloodGroup(registrationDto.getBloodGroup());
			supervisorDetails.setAddress(registrationDto.getAddress());
			supervisorDetails.setAge(registrationDto.getAge());
			supervisorDetails.setTaluka(talukaDetails);
			supervisorDetails.setCommonLogin(commonLogin); // Link CommonLogin to SupervisorDetails
			supervisorDetails.setRegisteredBy(registeredByUser); // Store who registered this supervisor

			supervisorDetailsRepository.save(supervisorDetails);

			// Prepare response
			response.put("userId", commonLogin.getUserId());
			response.put("email", commonLogin.getEmail());
			response.put("role", commonLogin.getRole().getRoleName());
			response.put("supervisorId", supervisorDetails.getId());
			response.put("districtId", supervisorDetails.getTaluka().getDistrict().getDistrictId());
			response.put("talukaId", supervisorDetails.getTaluka().getTalukaId());

//	        Long registeredById = getLoggedInUserId();  // This method will fetch the logged-in user's ID (Supervisor or Co-worker)
//	        supervisorDetails.setRegisteredById(registeredById); // Set the registeredById

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
	@Transactional
	public ResponseEntity<ApiResponse<Map<String, Object>>> registerCoWorker(RegistrationDto registrationDto,
			Long coWId) {
		ApiResponse<Map<String, Object>> resp = new ApiResponse<>();
		Map<String, Object> response = new HashMap<>();
		Taluka talukaDetails = talukaRepo.findById(registrationDto.getTalukaId())
				.orElseThrow(() -> new ResourceNotFoundException(
						"Taluka with the Id" + registrationDto.getTalukaId() + " not found"));
		try {
			// Fetch Co-worker Role
			Optional<Role> roleOptional = roleRepository.findByRoleName("ROLE_COWORKER");
			if (roleOptional.isEmpty()) {
				resp.setStatus("FAILED");
				resp.setMessage("Co-worker role not found.");
				return ResponseEntity.badRequest().body(resp);
			}

			// Check if email already exists
			Optional<CommonLogin> existingUser = loginRepository.findByEmail(registrationDto.getEmail());
			if (existingUser.isPresent()) {
				resp.setStatus("FAILED");
				resp.setMessage("Email already exists.");
				return ResponseEntity.badRequest().body(resp);
			}

			Optional<CommonLogin> data = loginRepository.findById(coWId);

			// Encrypt the password
			String encryptedPassword = passwordEncoder.encode(registrationDto.getPassword());

			// Create CommonLogin entity
			CommonLogin commonLogin = new CommonLogin();
			commonLogin.setEmail(registrationDto.getEmail());
			commonLogin.setMobileNo(registrationDto.getMobileNo());
			commonLogin.setPassword(encryptedPassword);
			commonLogin.setRole(roleOptional.get()); // Assign Co-worker role

			loginRepository.save(commonLogin);

			// Create SupervisorDetails entity (Reusing for Co-workers)
			SupervisorDetails coworkerDetails = new SupervisorDetails();
			coworkerDetails.setFirstName(registrationDto.getFirstName());
			coworkerDetails.setLastName(registrationDto.getLastName());
			coworkerDetails.setGender(registrationDto.getGender());
			coworkerDetails.setBloodGroup(registrationDto.getBloodGroup());
			coworkerDetails.setAddress(registrationDto.getAddress());
			coworkerDetails.setAge(registrationDto.getAge());
			coworkerDetails.setTaluka(talukaDetails);
			coworkerDetails.setCommonLogin(commonLogin);
			coworkerDetails.setRegisteredBy(data.get());
			supervisorDetailsRepository.save(coworkerDetails);

			// Prepare response
			response.put("userId", commonLogin.getUserId());
			response.put("email", commonLogin.getEmail());
			response.put("role", commonLogin.getRole().getRoleName());
			response.put("coworkerId", coworkerDetails.getId());
			response.put("districtId", coworkerDetails.getTaluka().getDistrict().getDistrictId());
			response.put("talukaId", coworkerDetails.getTaluka().getTalukaId());
			response.put("bloodGroup", coworkerDetails.getBloodGroup());
			response.put("address", coworkerDetails.getAddress());

			resp.setStatus("SUCCESS");
			resp.setMessage("Co-worker registered successfully.");
			resp.setData(response);

			return ResponseEntity.ok(resp);
		} catch (Exception e) {
			resp.setStatus("FAILED");
			resp.setMessage("Error while registering co-worker: " + e.getMessage());
			return ResponseEntity.internalServerError().body(resp);
		}
	}

	@Override
	@Transactional
	public ResponseEntity<Object> updateLaborDetails(Long commonLoginId, RegistrationDto laborUpdateRequest) {
		ApiResponse<Map<String, Object>> resp = new ApiResponse<>();
		Map<String, Object> response = new HashMap<>();

		// Fetch the CommonLogin (laborer) by ID
		Optional<CommonLogin> commonLoginOptional = loginRepository.findById(commonLoginId);
		if (!commonLoginOptional.isPresent()) {
			resp.setStatus(CommonMessages.FAILED);
			resp.setMessage("Laborer not found.");
			return new ResponseEntity<>(resp, HttpStatus.NOT_FOUND);
		}

		CommonLogin commonLogin = commonLoginOptional.get();

		// Fetch the SupervisorDetails to update
		Optional<SupervisorDetails> supervisorDetailsOptional = supervisorDetailsRepository
				.findByCommonLogin(commonLogin);
		if (!supervisorDetailsOptional.isPresent()) {
			resp.setStatus(CommonMessages.FAILED);
			resp.setMessage("Labor details not found.");
			return new ResponseEntity<>(resp, HttpStatus.NOT_FOUND);
		}

		SupervisorDetails supervisorDetails = supervisorDetailsOptional.get();

		// Update only the fields that are not null in the DTO
		if (laborUpdateRequest.getAddress() != null && !laborUpdateRequest.getAddress().trim().isEmpty()) {
			supervisorDetails.setAddress(laborUpdateRequest.getAddress());
		}
		if (laborUpdateRequest.getFirstName() != null && !laborUpdateRequest.getFirstName().trim().isEmpty()) {
			supervisorDetails.setFirstName(laborUpdateRequest.getFirstName());
		}
		if (laborUpdateRequest.getLastName() != null && !laborUpdateRequest.getLastName().trim().isEmpty()) {
			supervisorDetails.setLastName(laborUpdateRequest.getLastName());
		}
		if (laborUpdateRequest.getMedicalHistory() != null
				&& !laborUpdateRequest.getMedicalHistory().trim().isEmpty()) {
			supervisorDetails.setMedicalHistory(laborUpdateRequest.getMedicalHistory());
		}
		if (laborUpdateRequest.getProfileImage() != null && !laborUpdateRequest.getProfileImage().isEmpty()) {
			supervisorDetails.setProfileImage(laborUpdateRequest.getProfileImage().getOriginalFilename()); // Update

		}

		// Save the updated supervisor details
		supervisorDetailsRepository.save(supervisorDetails);

		// Prepare the response
		response.put("userId", commonLogin.getUserId());
		response.put("firstName", supervisorDetails.getFirstName());
		response.put("lastName", supervisorDetails.getLastName());
		response.put("address", supervisorDetails.getAddress());
		response.put("gender", supervisorDetails.getGender());
		response.put("bloodGroup", supervisorDetails.getBloodGroup());
		response.put("districtId", supervisorDetails.getTaluka().getDistrict().getDistrictId());
		response.put("talukaId", supervisorDetails.getTaluka().getTalukaId());
		response.put("profileImage", supervisorDetails.getProfileImage());

		resp.setStatus(CommonMessages.SUCCESS);
		resp.setMessage("Labor details updated successfully.");
		resp.setData(response);

		return new ResponseEntity<>(resp, HttpStatus.OK);
	}

	@Override
	@Transactional
	public ResponseEntity<ApiResponse<Map<String, Object>>> deleteLaborDetails(Long commonLoginId) {
		ApiResponse<Map<String, Object>> resp = new ApiResponse<>();
		Map<String, Object> response = new HashMap<>();

		// Fetch the CommonLogin (laborer) by ID
		Optional<CommonLogin> commonLoginOptional = loginRepository.findById(commonLoginId);
		if (!commonLoginOptional.isPresent()) {
			resp.setStatus("Failed");
			resp.setMessage("Labor not found");
			return new ResponseEntity<>(resp, HttpStatus.NOT_FOUND);
		}

		CommonLogin commonLogin = commonLoginOptional.get();

		// Fetch the associated SupervisorDetails to delete the laborer's details
		Optional<SupervisorDetails> supervisorDetailsOptional = supervisorDetailsRepository
				.findByCommonLogin(commonLogin);
		if (!supervisorDetailsOptional.isPresent()) {
			resp.setStatus("Failed");
			resp.setMessage("Labor details not found");
			return new ResponseEntity<>(resp, HttpStatus.NOT_FOUND);
		}

		SupervisorDetails supervisorDetails = supervisorDetailsOptional.get();

		// Delete the laborer's details
		supervisorDetailsRepository.delete(supervisorDetails); // Deleting the laborer's details

		// Prepare the response
		response.put("message", "Laborer deleted successfully");
		resp.setStatus("Success");
		resp.setMessage("Laborer deleted successfully");
		resp.setData(response);

		return new ResponseEntity<>(resp, HttpStatus.OK);
	}

	@Override
	@Transactional
	public ResponseEntity<ApiResponse<Map<String, Object>>> registerLabor(RegistrationDto laborDto, Long cowId) {
		ApiResponse<Map<String, Object>> resp = new ApiResponse<>();
		Map<String, Object> response = new HashMap<>();
		Taluka talukaDetails = talukaRepo.findById(laborDto.getTalukaId()).orElseThrow(
				() -> new ResourceNotFoundException("Taluka with the Id" + laborDto.getTalukaId() + " not found"));
		try {
			// Fetch the Role from the DTO
			Optional<Role> roleOptional = roleRepository.findById(laborDto.getRoleId());
			if (roleOptional.isEmpty()) {
				resp.setStatus("FAILED");
				resp.setMessage("Role not found");
				return ResponseEntity.badRequest().body(resp);
			}
			Role role = roleOptional.get();

			// Check if the laborer's email already exists
			Optional<CommonLogin> existingLabor = loginRepository.findByEmail(laborDto.getEmail());
			if (existingLabor.isPresent()) {
				resp.setStatus("FAILED");
				resp.setMessage("Laborer with this email already exists");
				return ResponseEntity.badRequest().body(resp);
			}

			// Encrypt the password
			String encryptedPassword = passwordEncoder.encode(laborDto.getPassword());

			// Create CommonLogin entity for labor
			CommonLogin labor = new CommonLogin();
			labor.setEmail(laborDto.getEmail());
			labor.setMobileNo(laborDto.getMobileNo());
			labor.setPassword(encryptedPassword);
			labor.setRole(role); // Assign role from DTO

			loginRepository.save(labor);

			Optional<CommonLogin> cl = loginRepository.findById(cowId);

			// Reuse SupervisorDetails entity for Labor
			SupervisorDetails laborDetails = new SupervisorDetails(); // Reusing SupervisorDetails
			laborDetails.setFirstName(laborDto.getFirstName());
			laborDetails.setLastName(laborDto.getLastName());
			laborDetails.setGender(laborDto.getGender());
			laborDetails.setBloodGroup(laborDto.getBloodGroup());
			laborDetails.setAddress(laborDto.getAddress());
			laborDetails.setCommonLogin(labor); // Link CommonLogin to SupervisorDetails
			laborDetails.setAge(laborDto.getAge());
			laborDetails.setFamilyMembers(laborDto.getFamilyMembers());
			laborDetails.setMedicalHistory(laborDto.getMedicalHistory());
			laborDetails.setTaluka(talukaDetails);
			laborDetails.setRegisteredBy(cl.get());
			// Handle profile image if present
			if (laborDto.getProfileImage() != null && !laborDto.getProfileImage().isEmpty()) {
				String profileImageUrl = commonFunctions.saveLaborImage(laborDto.getProfileImage()); // Pass the image
																										// here
				laborDetails.setProfileImage(profileImageUrl); // Set profile image URL in labor details
			}

//	        // Generate the unique labor ID
//	        String uniqueLaborId = UUID.randomUUID().toString();
//	        laborDetails.setUniqueLaborId(uniqueLaborId);

			// Generate the unique labor ID by concatenating "LBR-" with the uuid
			String uniqueLaborId = labor.getUuid();
			laborDetails.setUniqueLaborId(uniqueLaborId); // Set the unique labor ID

			supervisorDetailsRepository.save(laborDetails);

			// Prepare response with full details
			response.put("firstName", laborDetails.getFirstName());
			response.put("lastName", laborDetails.getLastName());
			response.put("gender", laborDetails.getGender());
			response.put("bloodGroup", laborDetails.getBloodGroup());
			response.put("address", laborDetails.getAddress());
			response.put("email", labor.getEmail());
			response.put("mobileNo", labor.getMobileNo());
			response.put("role", labor.getRole().getRoleName());
			response.put("districtId", laborDetails.getTaluka().getDistrict().getDistrictId());
			response.put("talukaId", laborDetails.getTaluka().getTalukaId());
			response.put("userId", labor.getUserId());
			response.put("laborId", laborDetails.getId());
			response.put("medicalHistory", laborDetails.getMedicalHistory());
			response.put("age", laborDetails.getAge());
			response.put("familyMembers", laborDetails.getFamilyMembers());
			response.put("uniqueLaborId", laborDetails.getUniqueLaborId());

//	        Long registeredById = getLoggedInUserId();  // This method will fetch the logged-in user's ID (Supervisor or Co-worker)
//	        laborDetails.setRegisteredById(registeredById); // Set the registeredById

			// Include the profile image URL if available
			if (laborDetails.getProfileImage() != null) {
				response.put("profileImageUrl", laborDetails.getProfileImage());
			}

			resp.setStatus("SUCCESS");
			resp.setMessage("Labor registered successfully.");
			resp.setData(response);

			return ResponseEntity.ok(resp);
		} catch (Exception e) {
			// In case of an error, print the exception details for debugging
			log.error("Error while registering labor: ", e);

			resp.setStatus("FAILED");
			resp.setMessage("Error while registering labor: " + e.getMessage());
			resp.setData(null); // Ensure data is null in case of failure

			return ResponseEntity.internalServerError().body(resp);
		}
	}

	@Override
	@Transactional
	public ResponseEntity<ApiResponse<Map<String, Object>>> deactivateUser(Long userId) {
		ApiResponse<Map<String, Object>> response = new ApiResponse<>();
		Map<String, Object> data = new HashMap<>();

		try {
			// Fetch CommonLogin details using userId
			CommonLogin commonLogin = loginRepository.findByUserId(userId);
			if (commonLogin == null) {
				response.setStatus("FAILED");
				response.setMessage("User not found.");
				return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
			}

			// Check if the user is already inactive
			if (UserStatus.IN_ACTIVE.equals(commonLogin.getStatus())) {
				response.setStatus("FAILED");
				response.setMessage("User is already inactive.");
				return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
			}

			// Update user status to INACTIVE
			commonLogin.setStatus(UserStatus.IN_ACTIVE);
			loginRepository.save(commonLogin);

			// Success response
			response.setStatus("SUCCESS");
			response.setMessage("User deactivated successfully.");
			data.put("userId", commonLogin.getUserId());
			data.put("status", "inactive");
			response.setData(data);

			return new ResponseEntity<>(response, HttpStatus.OK);

		} catch (Exception e) {
			// Error handling
			response.setStatus("FAILED");
			response.setMessage("Error deactivating user: " + e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	@Transactional
	public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getAllLaborDetails() {
		ApiResponse<List<Map<String, Object>>> response = new ApiResponse<>();
		List<Map<String, Object>> laborList = new ArrayList<>();

		try {
			// Fetch all laborers from the database
			List<CommonLogin> labors = loginRepository.findByRole_RoleName("ROLE_LABOR");

			if (labors.isEmpty()) {
				response.setStatus("FAILED");
				response.setMessage("No laborers found.");
				return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
			}

			// Process each labor record
			for (CommonLogin commonLogin : labors) {
				Optional<SupervisorDetails> supervisorDetailsOpt = supervisorDetailsRepository
						.findByCommonLogin(commonLogin);

				if (!supervisorDetailsOpt.isPresent()) {
					continue; // Skip if no details found
				}

				SupervisorDetails supervisorDetails = supervisorDetailsOpt.get();
				Map<String, Object> laborData = new HashMap<>();

				laborData.put("userId", commonLogin.getUserId());
				laborData.put("email", commonLogin.getEmail());
				laborData.put("role", commonLogin.getRole().getRoleName());
				laborData.put("mobileNo", commonLogin.getMobileNo());
				laborData.put("firstName", supervisorDetails.getFirstName());
				laborData.put("lastName", supervisorDetails.getLastName());
				laborData.put("gender", supervisorDetails.getGender());
				laborData.put("bloodGroup", supervisorDetails.getBloodGroup());
				laborData.put("address", supervisorDetails.getAddress());
				laborData.put("districtId", supervisorDetails.getTaluka().getDistrict().getDistrictId());
				laborData.put("talukaId", supervisorDetails.getTaluka().getTalukaId());
				laborData.put("age", supervisorDetails.getAge());
				laborData.put("familyMembers", supervisorDetails.getFamilyMembers());
				laborData.put("medicalHistory", supervisorDetails.getMedicalHistory());
				laborData.put("uniqueLaborId", supervisorDetails.getUniqueLaborId());

				// Fetch associated documents
				List<Document> documents = documentRepository.findByCommonLogin(commonLogin);

				if (!documents.isEmpty()) {
					List<Map<String, String>> documentDetails = new ArrayList<>();

					for (Document document : documents) {
						Map<String, String> documentInfo = new HashMap<>();
						documentInfo.put("documentType", document.getDocumentType().name());
						documentInfo.put("documentLink", document.getDocumentLink());
						documentDetails.add(documentInfo);
					}

					laborData.put("documents", documentDetails);
				} else {
					laborData.put("documents", "No documents uploaded");
				}

				laborList.add(laborData);
			}

			response.setStatus("SUCCESS");
			response.setMessage("Labor details fetched successfully.");
			response.setData(laborList);

			return new ResponseEntity<>(response, HttpStatus.OK);

		} catch (Exception e) {
			response.setStatus("FAILED");
			response.setMessage("Error fetching labor details: " + e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
