package com.sugarcanelabour.serviceimpl;

import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.sugarcanelabour.entity.CommonLogin;
import com.sugarcanelabour.entity.Document;
import com.sugarcanelabour.entity.SupervisorDetails;
import com.sugarcanelabour.helper.ApiResponse;
import com.sugarcanelabour.helper.CommonMessages;
import com.sugarcanelabour.model.LaboursDto;
import com.sugarcanelabour.repository.CommonLoginRepository;
import com.sugarcanelabour.repository.DocumentRepository;
import com.sugarcanelabour.repository.SupervisorDetailsRepository;
import com.sugarcanelabour.service.SupervisorService;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SupervisorServiceImpl implements SupervisorService{
	
	@Autowired
	private CommonLoginRepository loginRepository;

	@Autowired
	private SupervisorDetailsRepository supervisorDetailsRepository;

	@Autowired
	private DocumentRepository documentRepository;

	@Override
	public ResponseEntity<ApiResponse<Map<String, Object>>> getCountByAllRoles() {
		  ApiResponse<Map<String, Object>> resp = new ApiResponse<>();
		    Map<String, Object> response = new LinkedHashMap<>(); // Ensure order

		    try {
		        // Define the expected sequence
		        List<String> roleOrder = Arrays.asList("ROLE_COWORKER", "ROLE_LABOUR");

		        // Initialize a map to store the user count for each role
		        Map<String, Long> roleUserCountMap = new LinkedHashMap<>();

		        // Populate counts in a fixed sequence
		        for (String roleName : roleOrder) {
		            Long count = loginRepository.countByRole_RoleName(roleName);
		            roleUserCountMap.put(roleName, count != null ? count : 0); // Avoid null values
		        }

		        // Add the role-user count map to the response
		        response.put("roleUserCount", roleUserCountMap);

		        resp.setStatus(CommonMessages.SUCCESS);
		        resp.setMessage("Successfully fetched user count for all roles.");
		        resp.setData(response);

		        return ResponseEntity.ok(resp);
		    } catch (Exception e) {
		        resp.setStatus(CommonMessages.FAILED);
		        resp.setMessage("Error fetching user count: " + e.getMessage());
		        return ResponseEntity.internalServerError().body(resp);
		    }
	}

	@Override
	public ResponseEntity<ApiResponse<Map<String, Object>>> getCountByMonth() {
		  ApiResponse<Map<String, Object>> resp = new ApiResponse<>();
		    Map<String, Object> response = new HashMap<>();

		    try {
		        List<Object[]> result = loginRepository.findCountByMonth_Role();

		        // Initialize a map to store the grouped results
		        Map<String, List<Map<String, Object>>> groupedByMonth = new LinkedHashMap<>();

		        // Process the result list to organize the data
		        for (Object[] row : result) {
		            Integer month = (Integer) row[0];  // Get the month
		            String role = (String) row[1];     // Get the role name
		            Long registrationCount = (Long) row[2];  // Get the registration count

		            // Format month as string (e.g., "January", "February")
		            String monthString = Month.of(month).name();

		            // Prepare the map to hold the current entry
		            Map<String, Object> data = new HashMap<>();
		            data.put("role", role);
		            data.put("registrationCount", registrationCount);

		            // Add this data to the map grouped by month
		            groupedByMonth.computeIfAbsent(monthString, k -> new ArrayList<>()).add(data);
		        }

		        // Put the grouped results into the response
		        response.put("monthlyRegistrations", groupedByMonth);
		        resp.setStatus(CommonMessages.SUCCESS);
		        resp.setMessage("Registration count by month retrieved successfully.");
		        resp.setData(response);

		        return ResponseEntity.ok(resp);
		    } catch (Exception e) {
		        resp.setStatus(CommonMessages.FAILED);
		        resp.setMessage("Error fetching registration count by month: " + e.getMessage());
		        return ResponseEntity.internalServerError().body(resp);
		    }
	}

	@Override
	public ResponseEntity<ApiResponse<List<LaboursDto>>> getLatestLaborDetails() {
		 ApiResponse<List<LaboursDto>> resp = new ApiResponse<>();
	        
	        try {
	            // Define Pageable object for pagination (10 latest labors sorted by createdAt DESC)
	            Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Order.desc("createdAt")));
	            
	            // Fetch the latest labors using the repository
	            Page<LaboursDto> laborPage = loginRepository.findLatestLabors(pageable);

	            // Check if there are no labors found
	            if (!laborPage.hasContent()) {
	                resp.setStatus(CommonMessages.FAILED);
	                resp.setMessage(CommonMessages.L_error);
	                return ResponseEntity.ok(resp);
	            }

	            // Set success status and the list of labors
	            resp.setStatus(CommonMessages.SUCCESS);
	            resp.setMessage(CommonMessages.L_success);
	            resp.setData(laborPage.getContent()); // Extract list of labors

	            return ResponseEntity.ok(resp);
	        } catch (Exception e) {
	            // Handle any exceptions and return an error response
	            resp.setStatus(CommonMessages.FAILED);
	            resp.setMessage(CommonMessages.L_Ef);
	            return ResponseEntity.internalServerError().body(resp);
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
	                    coworkerData.put("DistrictName", coworkerDetails.getTaluka().getDistrict().getDistrictName());
	                    coworkerData.put("talukaId", coworkerDetails.getTaluka().getTalukaId());
	                    coworkerData.put("TalukaName", coworkerDetails.getTaluka().getTalukaName());

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
	     log.info("Fetching all labours...");

	     ApiResponse<Map<String, Object>> response = new ApiResponse<>();
	     Map<String, Object> responseData = new HashMap<>();
	     List<Map<String, Object>> labourList = new ArrayList<>();

	     try {
	         // Fetch all CommonLogin records with role "ROLE_LABOR"
	         List<CommonLogin> labours = loginRepository.findByRole_RoleName("ROLE_LABOUR");

	         if (labours.isEmpty()) {
	             response.setStatus(CommonMessages.FAILED);
	             response.setMessage(CommonMessages.NLF);
	             return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
	         }

	         // Loop through all the labors and fetch their corresponding SupervisorDetails (used for labor data)
	         for (CommonLogin labour : labours) {
	             Optional<SupervisorDetails> supervisorDetailsOpt = supervisorDetailsRepository.findByCommonLogin(labour);

	             if (supervisorDetailsOpt.isPresent()) {
	                 SupervisorDetails supervisorDetails = supervisorDetailsOpt.get();
	                 Map<String, Object> laborData = new HashMap<>();

	                 // Populate labor data
	                 laborData.put("userId", labour.getUserId());
	                 laborData.put("email", labour.getEmail());
	                 laborData.put("role", labour.getRole().getRoleName());
	                 laborData.put("mobileNo", labour.getMobileNo());
	                 laborData.put("firstName", supervisorDetails.getFirstName());
	                 laborData.put("lastName", supervisorDetails.getLastName());
	                 laborData.put("gender", supervisorDetails.getGender());
	                 laborData.put("bloodGroup", supervisorDetails.getBloodGroup());
	                 laborData.put("address", supervisorDetails.getAddress());
	                 laborData.put("districtId", supervisorDetails.getTaluka().getDistrict().getDistrictId());
	                 laborData.put("DistrictName", supervisorDetails.getTaluka().getDistrict().getDistrictName());
	                 laborData.put("talukaId", supervisorDetails.getTaluka().getTalukaId());
	                 laborData.put("TalukaName", supervisorDetails.getTaluka().getTalukaName());
	                 laborData.put("age", supervisorDetails.getAge());
	                 laborData.put("familyMembers", supervisorDetails.getFamilyMembers());
	                 laborData.put("medicalHistory", supervisorDetails.getMedicalHistory());
	                 laborData.put("uniqueLaborId", supervisorDetails.getUniqueLaborId());

	                 // Fetch documents associated with the labor
	                 List<Document> documents = documentRepository.findByCommonLogin(labour);

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
	                 labourList.add(laborData);
	             }
	         }

	         // Prepare response data
	         responseData.put("count", labourList.size());
	         responseData.put("labors", labourList);

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

	@Override
	public ResponseEntity<ApiResponse<Map<String, Object>>> getCoworkerById(Long commonLoginId) {
		ApiResponse<Map<String, Object>> response = new ApiResponse<>();
		Map<String, Object> data = new HashMap<>();

		try {
			// Fetch CommonLogin details
			CommonLogin commonLogin = loginRepository.findById(commonLoginId)
					.orElseThrow(() -> new RuntimeException("Coworker not found"));

			// Check if the role is COWORKER
			if (!"ROLE_COWORKER".equalsIgnoreCase(commonLogin.getRole().getRoleName())) {
				response.setStatus(CommonMessages.FAILED);
				response.setMessage(CommonMessages.U_not_coworker);
				return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
			}

			// Fetch supervisor details (used for both supervisors and coworkers)
			Optional<SupervisorDetails> supervisorDetailsOpt = supervisorDetailsRepository
					.findByCommonLogin(commonLogin);

			if (!supervisorDetailsOpt.isPresent()) {
				response.setStatus(CommonMessages.FAILED);
				response.setMessage(CommonMessages.CD_not_found);
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
			data.put("DistrictName", supervisorDetails.getTaluka().getDistrict().getDistrictName());
			data.put("talukaId", supervisorDetails.getTaluka().getTalukaId());
			data.put("TalukaName", supervisorDetails.getTaluka().getTalukaName());

			// Success response
			response.setStatus(CommonMessages.SUCCESS);
			response.setMessage(CommonMessages.C_Fetch_Successs);
			response.setData(data);

			return new ResponseEntity<>(response, HttpStatus.OK);

		} catch (Exception e) {
			// Error handling
			response.setStatus(CommonMessages.FAILED);
			response.setMessage(CommonMessages.Error_Fetch_Coworker);
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	
	}

	@Override
	public ResponseEntity<ApiResponse<Map<String, Object>>> getLabourById(Long commonLoginId) {
		ApiResponse<Map<String, Object>> response = new ApiResponse<>();
		Map<String, Object> data = new HashMap<>();

		try {
			// Fetch CommonLogin details
			CommonLogin commonLogin = loginRepository.findById(commonLoginId)
					.orElseThrow(() -> new RuntimeException(CommonMessages.L_NF));

			// Check if the role is LABOR
			if (!"ROLE_LABOUR".equalsIgnoreCase(commonLogin.getRole().getRoleName())) {
				response.setStatus(CommonMessages.FAILED);
				response.setMessage(CommonMessages.U_Not_Labour);
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
			data.put("DistrictName", supervisorDetails.getTaluka().getDistrict().getDistrictName());
			data.put("talukaId", supervisorDetails.getTaluka().getTalukaId());
			data.put("TalukaName", supervisorDetails.getTaluka().getTalukaName());
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
}
