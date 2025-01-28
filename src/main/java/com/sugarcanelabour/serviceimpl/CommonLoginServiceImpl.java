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


    @Override
    public ResponseEntity<Object> registerSuperAdmin(SuperAdminRegistrationDto superAdminDto) {
        Map<String, Object> response = new HashMap<>();
        try {
            // Encrypt the password
            String encryptedPassword = passwordEncoder.encode(superAdminDto.getPassword());

            // Fetch the role by ID
            Optional<Role> roleOptional = roleRepository.findById(superAdminDto.getRoleId());
            if (roleOptional.isEmpty()) {
                response.put("status", "FAILED");
                response.put("message", "Role not found");
                return ResponseEntity.badRequest().body(response);
            }

            Role role = roleOptional.get();

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
            superAdmin.setPassword(encryptedPassword);
            superAdmin.setRole(role); // Assign the role

            CommonLogin savedSuperAdmin = loginRepository.save(superAdmin);

            response.put("status", "SUCCESS");
            response.put("message", "Super Admin registered successfully");
            response.put("userId", savedSuperAdmin.getUserId());
            response.put("email", savedSuperAdmin.getEmail());
            response.put("role", savedSuperAdmin.getRole().getRoleName());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "FAILED");
            response.put("message", "Error while registering Super Admin: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }





     // supervisor register

    @Override
    public ResponseEntity<Object> registerSupervisor(RegistrationDto supervisorDto) {
        Map<String, Object> response = new HashMap<>();
        try {
            // Encrypt the password
            String encryptedPassword = passwordEncoder.encode(supervisorDto.getPassword());

            // Fetch the role from the roles table based on roleId from DTO
            Optional<Role> roleOptional = roleRepository.findById(supervisorDto.getRoleId());
            if (roleOptional.isEmpty()) {
                response.put("status", "FAILED");
                response.put("message", "Role not found");
                return ResponseEntity.badRequest().body(response);
            }

            Role role = roleOptional.get();

            // Check if the supervisor email already exists
            Optional<CommonLogin> existingSupervisor = loginRepository.findByEmail(supervisorDto.getEmail());
            if (existingSupervisor.isPresent()) {
                response.put("status", "FAILED");
                response.put("message", "Supervisor with this email already exists");
                return ResponseEntity.badRequest().body(response);
            }

            // Create and save the new supervisor
            CommonLogin supervisor = new CommonLogin();
            supervisor.setEmail(supervisorDto.getEmail());
            supervisor.setMobileNo(supervisorDto.getMobileNo());
            supervisor.setPassword(encryptedPassword);
            supervisor.setRole(role); // Assign the role dynamically based on roleId

            CommonLogin savedSupervisor = loginRepository.save(supervisor);

            // Add response fields, including RegistrationDto fields
            response.put("status", "SUCCESS");
            response.put("message", "Supervisor registered successfully");
            response.put("userId", savedSupervisor.getUserId());
            response.put("email", savedSupervisor.getEmail());
            response.put("role", savedSupervisor.getRole().getRoleName());
            response.put("firstName", supervisorDto.getFirstName());
            response.put("lastName", supervisorDto.getLastName());
            response.put("gender", supervisorDto.getGender());
            response.put("bloodGroup", supervisorDto.getBloodGroup());
            response.put("address", supervisorDto.getAddress());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "FAILED");
            response.put("message", "Error while registering Supervisor: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    
    //register co-worker

    @Override
    public ResponseEntity<Object> registerCoworker(RegistrationDto coworkerDto) {
        Map<String, Object> response = new HashMap<>();
        try {
            // Encrypt the password
            String encryptedPassword = passwordEncoder.encode(coworkerDto.getPassword());

            // Fetch the role from the DTO (assuming the role is passed via the DTO)
            Optional<Role> roleOptional = roleRepository.findById(coworkerDto.getRoleId());
            if (roleOptional.isEmpty()) {
                response.put("status", "FAILED");
                response.put("message", "Role not found");
                return ResponseEntity.badRequest().body(response);
            }

            Role role = roleOptional.get();

            // Check if the coworker's email already exists
            Optional<CommonLogin> existingCoworker = loginRepository.findByEmail(coworkerDto.getEmail());
            if (existingCoworker.isPresent()) {
                response.put("status", "FAILED");
                response.put("message", "Coworker with this email already exists");
                return ResponseEntity.badRequest().body(response);
            }

            // Create and save the new coworker
            CommonLogin coworker = new CommonLogin();
            coworker.setEmail(coworkerDto.getEmail());
            coworker.setMobileNo(coworkerDto.getMobileNo());
            coworker.setPassword(encryptedPassword);
            coworker.setRole(role); // Assign the role from DTO

            CommonLogin savedCoworker = loginRepository.save(coworker);

            // Add response fields
            response.put("status", "SUCCESS");
            response.put("message", "Coworker registered successfully");
            response.put("userId", savedCoworker.getUserId());
            response.put("email", savedCoworker.getEmail());
            response.put("role", savedCoworker.getRole().getRoleName());
            response.put("firstName", coworkerDto.getFirstName());
            response.put("lastName", coworkerDto.getLastName());
            response.put("gender", coworkerDto.getGender());
            response.put("bloodGroup", coworkerDto.getBloodGroup());
            response.put("address", coworkerDto.getAddress());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "FAILED");
            response.put("message", "Error while registering Coworker: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }


    @Override
    public ResponseEntity<Object> registerLaborer(RegistrationDto laborerDto) {
        Map<String, Object> response = new HashMap<>();
        try {
            // Encrypt the password
            String encryptedPassword = passwordEncoder.encode(laborerDto.getPassword());

            // Fetch the laborer role from the DTO
            Optional<Role> roleOptional = roleRepository.findById(laborerDto.getRoleId());
            if (roleOptional.isEmpty()) {
                response.put("status", "FAILED");
                response.put("message", "Role not found");
                return ResponseEntity.badRequest().body(response);
            }

            Role role = roleOptional.get();

            // Check if the laborer's email already exists
            Optional<CommonLogin> existingLaborer = loginRepository.findByEmail(laborerDto.getEmail());
            if (existingLaborer.isPresent()) {
                response.put("status", "FAILED");
                response.put("message", "Laborer with this email already exists");
                return ResponseEntity.badRequest().body(response);
            }

            // Create and save the new laborer
            CommonLogin laborer = new CommonLogin();
            laborer.setEmail(laborerDto.getEmail());
            laborer.setMobileNo(laborerDto.getMobileNo());
            laborer.setPassword(encryptedPassword);
            laborer.setRole(role); // Assign role from DTO

            CommonLogin savedLaborer = loginRepository.save(laborer);

            // Add response fields
            response.put("status", "SUCCESS");
            response.put("message", "Laborer registered successfully");
            response.put("userId", savedLaborer.getUserId());
            response.put("email", savedLaborer.getEmail());
            response.put("role", savedLaborer.getRole().getRoleName());
            response.put("firstName", laborerDto.getFirstName());
            response.put("lastName", laborerDto.getLastName());
            response.put("gender", laborerDto.getGender());
            response.put("bloodGroup", laborerDto.getBloodGroup());
            response.put("address", laborerDto.getAddress());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "FAILED");
            response.put("message", "Error while registering Laborer: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

	

	
}
