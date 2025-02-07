package com.sugarcanelabour.serviceimpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.sugarcanelabour.entity.CommonLogin;
import com.sugarcanelabour.entity.Document;
import com.sugarcanelabour.entity.SupervisorDetails;
import com.sugarcanelabour.helper.ApiResponse;
import com.sugarcanelabour.helper.Enums.UserStatus;
import com.sugarcanelabour.repository.CommonLoginRepository;
import com.sugarcanelabour.repository.DocumentRepository;
import com.sugarcanelabour.repository.SupervisorDetailsRepository;
import com.sugarcanelabour.service.SuperAdminService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SuperAdminServiceImpl implements SuperAdminService {

    @Autowired
    private CommonLoginRepository loginRepository;
    
    @Autowired
    private SupervisorDetailsRepository supervisorDetailsRepository;
    
    @Autowired
    private DocumentRepository documentRepository;

    @Override
    public ResponseEntity<ApiResponse<Map<String, Object>>> getAdminDetails(Long userId) {
        ApiResponse<Map<String, Object>> response = new ApiResponse<>();
        Map<String, Object> data = new HashMap<>();

        try {
            // Fetch Admin's CommonLogin details
            Optional<CommonLogin> adminOptional = loginRepository.findById(userId);
            if (adminOptional.isEmpty()) {
                response.setStatus("FAILED");
                response.setMessage("Admin not found");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }

            CommonLogin admin = adminOptional.get();

            // Check if the role is ADMIN
            if (!"ROLE_ADMIN".equalsIgnoreCase(admin.getRole().getRoleName())) {
                response.setStatus("FAILED");
                response.setMessage("User is not an admin.");
                return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
            }

            // Fetch admin's personal details
            Optional<SupervisorDetails> supervisorDetailsOpt = supervisorDetailsRepository.findByCommonLogin(admin);
            if (!supervisorDetailsOpt.isPresent()) {
                response.setStatus("FAILED");
                response.setMessage("Admin details not found");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }

            SupervisorDetails supervisorDetails = supervisorDetailsOpt.get();

            // Collect admin details
            data.put("userId", admin.getUserId());
            data.put("email", admin.getEmail());
            data.put("role", admin.getRole().getRoleName());
            data.put("firstName", supervisorDetails.getFirstName());
            data.put("lastName", supervisorDetails.getLastName());
            data.put("districtId", supervisorDetails.getDistrictId());
            data.put("talukaId", supervisorDetails.getTalukaId());

            // Success response
            response.setStatus("SUCCESS");
            response.setMessage("Admin details fetched successfully.");
            response.setData(data);

            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            // Error handling
            response.setStatus("FAILED");
            response.setMessage("Error fetching admin details: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    // Get Supervisor Details by userId
    @Override
    public ResponseEntity<ApiResponse<Map<String, Object>>> getSupervisorDetails(Long userId) {
        ApiResponse<Map<String, Object>> response = new ApiResponse<>();
        Map<String, Object> data = new HashMap<>();

        try {
            // Fetch Supervisor's CommonLogin details
            CommonLogin commonLogin = loginRepository.findByUserId(userId);
            if (commonLogin == null) {
                response.setStatus("FAILED");
                response.setMessage("Supervisor not found.");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }

            // Check if the role is SUPERVISOR
            if (!"ROLE_SUPERVISOR".equalsIgnoreCase(commonLogin.getRole().getRoleName())) {
                response.setStatus("FAILED");
                response.setMessage("User is not a supervisor.");
                return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
            }

            // Fetch SupervisorDetails by CommonLogin
            Optional<SupervisorDetails> supervisorDetailsOpt = supervisorDetailsRepository
                    .findByCommonLogin(commonLogin);
            if (!supervisorDetailsOpt.isPresent()) {
                response.setStatus("FAILED");
                response.setMessage("Supervisor details not found.");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }

            SupervisorDetails supervisorDetails = supervisorDetailsOpt.get();

            // Collect supervisor details
            data.put("userId", commonLogin.getUserId());
            data.put("email", commonLogin.getEmail());
            data.put("role", commonLogin.getRole().getRoleName());
            data.put("supervisorId", supervisorDetails.getId());
            data.put("firstName", supervisorDetails.getFirstName());
            data.put("lastName", supervisorDetails.getLastName());
            data.put("gender", supervisorDetails.getGender());
            data.put("bloodGroup", supervisorDetails.getBloodGroup());
            data.put("address", supervisorDetails.getAddress());
            data.put("districtId", supervisorDetails.getDistrictId());
            data.put("talukaId", supervisorDetails.getTalukaId());

            // Success response
            response.setStatus("SUCCESS");
            response.setMessage("Supervisor details fetched successfully.");
            response.setData(data);

            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            // Error handling
            response.setStatus("FAILED");
            response.setMessage("Error fetching supervisor details: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @Override
    public ResponseEntity<ApiResponse<Map<String, Object>>> getCoworkerDetails(Long commonLoginId) {
        ApiResponse<Map<String, Object>> response = new ApiResponse<>();
        Map<String, Object> data = new HashMap<>();

        try {
            // Fetch CommonLogin details
            CommonLogin commonLogin = loginRepository.findById(commonLoginId)
                    .orElseThrow(() -> new RuntimeException("Coworker not found"));

            // Check if the role is COWORKER
            if (!"ROLE_COWORKER".equalsIgnoreCase(commonLogin.getRole().getRoleName())) {
                response.setStatus("FAILED");
                response.setMessage("User is not a coworker.");
                return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
            }

            // Fetch supervisor details (used for both supervisors and coworkers)
            Optional<SupervisorDetails> supervisorDetailsOpt = supervisorDetailsRepository.findByCommonLogin(commonLogin);

            if (!supervisorDetailsOpt.isPresent()) {
                response.setStatus("FAILED");
                response.setMessage("Coworker details not found.");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }

            SupervisorDetails supervisorDetails = supervisorDetailsOpt.get();

            // Collect coworker details
            data.put("userId", commonLogin.getUserId());
            data.put("email", commonLogin.getEmail());
            data.put("role", commonLogin.getRole().getRoleName());
            data.put("coworkerId", supervisorDetails.getId());
            data.put("firstName", supervisorDetails.getFirstName());
            data.put("lastName", supervisorDetails.getLastName());
            data.put("gender", supervisorDetails.getGender());
            data.put("bloodGroup", supervisorDetails.getBloodGroup());
            data.put("address", supervisorDetails.getAddress());
            data.put("districtId", supervisorDetails.getDistrictId());
            data.put("talukaId", supervisorDetails.getTalukaId());

            // Success response
            response.setStatus("SUCCESS");
            response.setMessage("Coworker details fetched successfully.");
            response.setData(data);

            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            // Error handling
            response.setStatus("FAILED");
            response.setMessage("Error fetching coworker details: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @Override
    public ResponseEntity<ApiResponse<Map<String, Object>>> getLaborDetails(Long commonLoginId) {
        ApiResponse<Map<String, Object>> response = new ApiResponse<>();
        Map<String, Object> data = new HashMap<>();

        try {
            // Fetch CommonLogin details
            CommonLogin commonLogin = loginRepository.findById(commonLoginId)
                    .orElseThrow(() -> new RuntimeException("Labor not found"));

            // Check if the role is LABOR
            if (!"ROLE_LABOR".equalsIgnoreCase(commonLogin.getRole().getRoleName())) {
                response.setStatus("FAILED");
                response.setMessage("User is not a laborer.");
                return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
            }

            // Fetch supervisor details (reusing SupervisorDetails entity for labor)
            Optional<SupervisorDetails> supervisorDetailsOpt = supervisorDetailsRepository.findByCommonLogin(commonLogin);

            if (!supervisorDetailsOpt.isPresent()) {
                response.setStatus("FAILED");
                response.setMessage("Supervisor details not found.");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }

            SupervisorDetails supervisorDetails = supervisorDetailsOpt.get();

            // Collect labor details
            data.put("userId", commonLogin.getUserId());
            data.put("email", commonLogin.getEmail());
            data.put("role", commonLogin.getRole().getRoleName());
            data.put("mobileNo", commonLogin.getMobileNo());
            data.put("firstName", supervisorDetails.getFirstName());
            data.put("lastName", supervisorDetails.getLastName());
            data.put("gender", supervisorDetails.getGender());
            data.put("bloodGroup", supervisorDetails.getBloodGroup());
            data.put("address", supervisorDetails.getAddress());
            data.put("districtId", supervisorDetails.getDistrictId());
            data.put("talukaId", supervisorDetails.getTalukaId());
            data.put("age", supervisorDetails.getAge());
            data.put("familyMembers", supervisorDetails.getFamilyMembers());
            data.put("medicalHistory", supervisorDetails.getMedicalHistory());
            data.put("uniqueLaborId", supervisorDetails.getUniqueLaborId());

            // Fetch documents associated with the labor
            List<Document> documents = documentRepository.findByCommonLogin(commonLogin);

            if (!documents.isEmpty()) {
                List<Map<String, String>> documentDetails = new ArrayList<>();

                for (Document document : documents) {
                    Map<String, String> documentInfo = new HashMap<>();
                    documentInfo.put("documentType", document.getDocumentType().name());
                    documentInfo.put("documentLink", document.getDocumentLink());
                    documentDetails.add(documentInfo);
                }

                data.put("documents", documentDetails);
            } else {
                data.put("documents", "No documents uploaded");
            }

            // Success response
            response.setStatus("SUCCESS");
            response.setMessage("Labor details fetched successfully.");
            response.setData(data);

            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            // Error handling
            response.setStatus("FAILED");
            response.setMessage("Error fetching labor details: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @Override
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
            data.put("status", "Inactive");
            response.setData(data);

            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            // Error handling
            response.setStatus("FAILED");
            response.setMessage("Error deactivating user: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



    
}
