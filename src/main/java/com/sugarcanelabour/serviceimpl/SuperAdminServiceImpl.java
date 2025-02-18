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
import com.sugarcanelabour.helper.CommonMessages;
import com.sugarcanelabour.helper.Enums.UserStatus;
import com.sugarcanelabour.repository.CommonLoginRepository;
import com.sugarcanelabour.repository.DocumentRepository;
import com.sugarcanelabour.repository.SupervisorDetailsRepository;
import com.sugarcanelabour.service.SuperAdminService;

import jakarta.transaction.Transactional;
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
	@Transactional
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
			data.put("districtId", supervisorDetails.getTaluka().getDistrict().getDistrictId());
			data.put("talukaId", supervisorDetails.getTaluka().getTalukaId());

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
	@Transactional
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
			data.put("districtId", supervisorDetails.getTaluka().getDistrict().getDistrictId());
			data.put("talukaId", supervisorDetails.getTaluka().getTalukaId());

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
	@Transactional
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
			Optional<SupervisorDetails> supervisorDetailsOpt = supervisorDetailsRepository
					.findByCommonLogin(commonLogin);

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
			data.put("districtId", supervisorDetails.getTaluka().getDistrict().getDistrictId());
			data.put("talukaId", supervisorDetails.getTaluka().getTalukaId());

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
	@Transactional
	public ResponseEntity<ApiResponse<Map<String, Object>>> getLaborDetails(Long commonLoginId) {
		ApiResponse<Map<String, Object>> response = new ApiResponse<>();
		Map<String, Object> data = new HashMap<>();

		try {
			// Fetch CommonLogin details
			CommonLogin commonLogin = loginRepository.findById(commonLoginId)
					.orElseThrow(() -> new RuntimeException(CommonMessages.L_NF));

			// Check if the role is LABOR
			if (!"ROLE_LABOR".equalsIgnoreCase(commonLogin.getRole().getRoleName())) {
				response.setStatus("FAILED");
				response.setMessage("User is not a laborer.");
				return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
			}

			// Fetch supervisor details (reusing SupervisorDetails entity for labor)
			Optional<SupervisorDetails> supervisorDetailsOpt = supervisorDetailsRepository
					.findByCommonLogin(commonLogin);

			if (!supervisorDetailsOpt.isPresent()) {
				response.setStatus(CommonMessages.FAILED);
				response.setMessage(CommonMessages.S_DETAILS_NF);
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
			data.put("districtId", supervisorDetails.getTaluka().getDistrict().getDistrictId());
			data.put("talukaId", supervisorDetails.getTaluka().getTalukaId());
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
				data.put("documents", CommonMessages.N_D_U);
			}

			// Success response
			response.setStatus(CommonMessages.SUCCESS);
			response.setMessage(CommonMessages.L_S);
			response.setData(data);

			return new ResponseEntity<>(response, HttpStatus.OK);

		} catch (Exception e) {
			// Error handling
			response.setStatus(CommonMessages.FAILED);
			response.setMessage(CommonMessages.EFLD);
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
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
				response.setStatus(CommonMessages.FAILED);
				response.setMessage(CommonMessages.NOT_FOUND);
				return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
			}

			// Check if the user is already inactive
			if (UserStatus.IN_ACTIVE.equals(commonLogin.getStatus())) {
				response.setStatus(CommonMessages.FAILED);
				response.setMessage(CommonMessages.U_ALREADY_INACTIVE);
				return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
			}

			// Update user status to INACTIVE
			commonLogin.setStatus(UserStatus.IN_ACTIVE);
			loginRepository.save(commonLogin);

			// Success response
			response.setStatus(CommonMessages.SUCCESS);
			response.setMessage(CommonMessages.DS);
			data.put("userId", commonLogin.getUserId());
			data.put("status", "Inactive");
			response.setData(data);

			return new ResponseEntity<>(response, HttpStatus.OK);

		} catch (Exception e) {
			// Error handling
			response.setStatus(CommonMessages.FAILED);
			response.setMessage(CommonMessages.ERROR_DU);
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	 @Override
	    public ResponseEntity<ApiResponse<Map<String, Object>>> getAllAdminDetails() {
	        log.info("Fetching all Admin details...");
	        ApiResponse<Map<String, Object>> response = new ApiResponse<>();
	        Map<String, Object> responseData = new HashMap<>();
	        List<Map<String, Object>> adminList = new ArrayList<>();

	        try {
	            List<CommonLogin> admins = loginRepository.findByRole_RoleName("ROLE_ADMIN");

	            if (admins.isEmpty()) {
	                response.setStatus("FAILED");
	                response.setMessage("No Admins found.");
	                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
	            }

	            for (CommonLogin admin : admins) {
	                Map<String, Object> adminData = new HashMap<>();
	                adminData.put("userId", admin.getUserId());
	                adminData.put("email", admin.getEmail());
	                adminData.put("role", admin.getRole().getRoleName());

	                adminList.add(adminData);
	            }

	            responseData.put("count", adminList.size());
	            responseData.put("admins", adminList);

	            response.setStatus(CommonMessages.SUCCESS);
	            response.setMessage(CommonMessages.A_RETRIVED);
	            response.setData(responseData);

	            return new ResponseEntity<>(response, HttpStatus.OK);

	        } catch (Exception e) {
	            log.error("Error fetching Admins: {}", e.getMessage());
	            response.setStatus(CommonMessages.FAILED);
	            response.setMessage(CommonMessages.A_ERRORFETCH);
	            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
	        }
	    }

	 @Override
	    public ResponseEntity<ApiResponse<Map<String, Object>>> getAllSupervisors() {
	        log.info("Fetching all Supervisor details...");
	        ApiResponse<Map<String, Object>> response = new ApiResponse<>();
	        Map<String, Object> responseData = new HashMap<>();
	        List<Map<String, Object>> supervisorList = new ArrayList<>();

	        try {
	            List<CommonLogin> supervisors = loginRepository.findByRole_RoleName("ROLE_SUPERVISOR");

	            if (supervisors.isEmpty()) {
	                response.setStatus(CommonMessages.FAILED);
	                response.setMessage(CommonMessages.S_NOT_FOUND);
	                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
	            }

	            for (CommonLogin supervisor : supervisors) {
	                Optional<SupervisorDetails> supervisorDetailsOpt = supervisorDetailsRepository.findByCommonLogin(supervisor);

	                if (supervisorDetailsOpt.isPresent()) {
	                    SupervisorDetails supervisorDetails = supervisorDetailsOpt.get();
	                    Map<String, Object> supervisorData = new HashMap<>();
	                    
	                    supervisorData.put("userId", supervisor.getUserId());
	                    supervisorData.put("email", supervisor.getEmail());
	                    supervisorData.put("role", supervisor.getRole().getRoleName());
	                    supervisorData.put("supervisorId", supervisorDetails.getId());
	                    supervisorData.put("firstName", supervisorDetails.getFirstName());
	                    supervisorData.put("lastName", supervisorDetails.getLastName());
	                    supervisorData.put("gender", supervisorDetails.getGender());
	                    supervisorData.put("bloodGroup", supervisorDetails.getBloodGroup());
	                    supervisorData.put("address", supervisorDetails.getAddress());
	                    supervisorData.put("districtId", supervisorDetails.getTaluka().getDistrict().getDistrictId());
	                    supervisorData.put("talukaId", supervisorDetails.getTaluka().getTalukaId());

	                    supervisorList.add(supervisorData);
	                }
	            }

	            responseData.put("count", supervisorList.size());
	            responseData.put("supervisors", supervisorList);

	            response.setStatus("SUCCESS");
	            response.setMessage("Supervisors retrieved successfully.");
	            response.setData(responseData);

	            return new ResponseEntity<>(response, HttpStatus.OK);

	        } catch (Exception e) {
	            log.error("Error fetching Supervisors: {}", e.getMessage());
	            response.setStatus("FAILED");
	            response.setMessage("Error fetching Supervisors: " + e.getMessage());
	            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
	        }
	    }

	 @Override
	    public ResponseEntity<ApiResponse<Map<String, Object>>> getAllCoworkers() {
	        log.info("Fetching all Coworkers...");

	        ApiResponse<Map<String, Object>> response = new ApiResponse<>();
	        Map<String, Object> responseData = new HashMap<>();
	        List<Map<String, Object>> coworkerList = new ArrayList<>();

	        try {
	            // Fetch all CommonLogin records with role "ROLE_COWORKER"
	            List<CommonLogin> coworkers = loginRepository.findByRole_RoleName("ROLE_COWORKER");

	            if (coworkers.isEmpty()) {
	                response.setStatus(CommonMessages.FAILED);
	                response.setMessage(CommonMessages.C_NF);
	                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
	            }

	            // Loop through all the coworkers and fetch the corresponding SupervisorDetails (used for coworkers as well)
	            for (CommonLogin coworker : coworkers) {
	                Optional<SupervisorDetails> coworkerDetailsOpt = supervisorDetailsRepository.findByCommonLogin(coworker);

	                if (coworkerDetailsOpt.isPresent()) {
	                    SupervisorDetails coworkerDetails = coworkerDetailsOpt.get();
	                    Map<String, Object> coworkerData = new HashMap<>();

	                    coworkerData.put("userId", coworker.getUserId());
	                    coworkerData.put("email", coworker.getEmail());
	                    coworkerData.put("role", coworker.getRole().getRoleName());
	                    coworkerData.put("coworkerId", coworkerDetails.getId());
	                    coworkerData.put("firstName", coworkerDetails.getFirstName());
	                    coworkerData.put("lastName", coworkerDetails.getLastName());
	                    coworkerData.put("gender", coworkerDetails.getGender());
	                    coworkerData.put("Age", coworkerDetails.getAge());
	                    coworkerData.put("bloodGroup", coworkerDetails.getBloodGroup());
	                    coworkerData.put("address", coworkerDetails.getAddress());
	                    coworkerData.put("districtId", coworkerDetails.getTaluka().getDistrict().getDistrictId());
	                    coworkerData.put("talukaId", coworkerDetails.getTaluka().getTalukaId());

	                    coworkerList.add(coworkerData);
	                }
	            }

	            // Prepare the response data
	            responseData.put("count", coworkerList.size());
	            responseData.put("coworkers", coworkerList);

	            response.setStatus(CommonMessages.SUCCESS);
	            response.setMessage(CommonMessages.CRS);
	            response.setData(responseData);

	            return new ResponseEntity<>(response, HttpStatus.OK);

	        } catch (Exception e) {
	            log.error("Error fetching Coworkers: {}", e.getMessage());
	            response.setStatus(CommonMessages.FAILED);
	            response.setMessage(CommonMessages.C_EF);
	            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
	        }
	    }
	 
	 @Override
	 @Transactional
	 public ResponseEntity<ApiResponse<Map<String, Object>>> getAllLabours() {
	     log.info("Fetching all labors...");

	     ApiResponse<Map<String, Object>> response = new ApiResponse<>();
	     Map<String, Object> responseData = new HashMap<>();
	     List<Map<String, Object>> laborList = new ArrayList<>();

	     try {
	         // Fetch all CommonLogin records with role "ROLE_LABOR"
	         List<CommonLogin> labors = loginRepository.findByRole_RoleName("ROLE_LABOR");

	         if (labors.isEmpty()) {
	             response.setStatus(CommonMessages.FAILED);
	             response.setMessage(CommonMessages.NLF);
	             return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
	         }

	         // Loop through all the labors and fetch their corresponding SupervisorDetails (used for labor data)
	         for (CommonLogin labor : labors) {
	             Optional<SupervisorDetails> supervisorDetailsOpt = supervisorDetailsRepository.findByCommonLogin(labor);

	             if (supervisorDetailsOpt.isPresent()) {
	                 SupervisorDetails supervisorDetails = supervisorDetailsOpt.get();
	                 Map<String, Object> laborData = new HashMap<>();

	                 // Populate labor data
	                 laborData.put("userId", labor.getUserId());
	                 laborData.put("email", labor.getEmail());
	                 laborData.put("role", labor.getRole().getRoleName());
	                 laborData.put("mobileNo", labor.getMobileNo());
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

	                 // Fetch documents associated with the labor
	                 List<Document> documents = documentRepository.findByCommonLogin(labor);

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
	                     laborData.put("documents",CommonMessages.N_D_U);
	                 }

	                 // Add labor data to the labor list
	                 laborList.add(laborData);
	             }
	         }

	         // Prepare response data
	         responseData.put("count", laborList.size());
	         responseData.put("labors", laborList);

	         response.setStatus(CommonMessages.SUCCESS);
	         response.setMessage(CommonMessages.L_DS);
	         response.setData(responseData);

	         return new ResponseEntity<>(response, HttpStatus.OK);

	     } catch (Exception e) {
	         log.error("Error fetching labors: {}", e.getMessage());
	         response.setStatus(CommonMessages.FAILED);
	         response.setMessage(CommonMessages.EFL);
	         return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
	     }
	 }

}
