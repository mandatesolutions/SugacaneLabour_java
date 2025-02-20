package com.sugarcanelabour.serviceimpl;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
import com.sugarcanelabour.service.CoworkerService;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CoworkerServiceImpl implements CoworkerService{
	

	private CommonLoginRepository loginRepository;
	private SupervisorDetailsRepository supervisorDetailsRepository;
	private DocumentRepository documentRepository;
	
	
	
	public CoworkerServiceImpl(CommonLoginRepository loginRepository,
			SupervisorDetailsRepository supervisorDetailsRepository, DocumentRepository documentRepository) {
		this.loginRepository = loginRepository;
		this.supervisorDetailsRepository = supervisorDetailsRepository;
		this.documentRepository = documentRepository;
	}

	@Override
	public ResponseEntity<ApiResponse<Map<String, Object>>> getCountByAllRoles() {
		  ApiResponse<Map<String, Object>> resp = new ApiResponse<>();
		    Map<String, Object> response = new LinkedHashMap<>(); // Ensure order

		    try {
		        // Define the expected sequence
		        List<String> roleOrder = Arrays.asList("ROLE_LABOUR");

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
		        List<Object[]> result = loginRepository.findCountBy_Month_Role();

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
	 @Transactional
	 public ResponseEntity<ApiResponse<Map<String, Object>>> getAllLabours() {
	     log.info("Fetching all labors...");

	     ApiResponse<Map<String, Object>> response = new ApiResponse<>();
	     Map<String, Object> responseData = new HashMap<>();
	     List<Map<String, Object>> laborList = new ArrayList<>();

	     try {
	         // Fetch all CommonLogin records with role "ROLE_LABOR"
	         List<CommonLogin> labors = loginRepository.findByRole_RoleName("ROLE_LABOUR");

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
	                 laborData.put("DistrictName", supervisorDetails.getTaluka().getDistrict().getDistrictName());
	                 laborData.put("talukaId", supervisorDetails.getTaluka().getTalukaId());
	                 laborData.put("TalukaName", supervisorDetails.getTaluka().getTalukaName());
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

	 //get By Id
	 
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
	
	//GET DAY COUNT

	@Override
	public ResponseEntity<ApiResponse<Map<String, Object>>> getTodaysCount() {
	    ApiResponse<Map<String, Object>> resp = new ApiResponse<>();
	    Map<String, Object> response = new HashMap<>();

	    try {
	        List<Object[]> result = loginRepository.findTodayRegistrationCount();

	        // Store count by role
	        Map<String, Long> roleCounts = new HashMap<>();

	        // Process the result list
	        for (Object[] row : result) {
	            String role = (String) row[0]; // Get the role name
	            Long registrationCount = (Long) row[1]; // Get the registration count
	            roleCounts.put(role, registrationCount);
	        }

	        // Define all roles to ensure zero count is shown for roles with no registrations
	        List<String> allRoles = List.of("ROLE_LABOUR"); // Add all roles

	        List<Map<String, Object>> todayRegistrations = new ArrayList<>();

	        // Loop through all roles and ensure zero count where necessary
	        for (String role : allRoles) {
	            Map<String, Object> data = new HashMap<>();
	            data.put("role", role);
	            data.put("registrationCount", roleCounts.getOrDefault(role, 0L)); // Default to 0 if not found
	            todayRegistrations.add(data);
	        }

	        response.put("date", LocalDate.now().toString()); // Add today's date
	        response.put("todayRegistrations", todayRegistrations);
	        

	        resp.setStatus(CommonMessages.SUCCESS);
	        resp.setMessage(CommonMessages.Count_Retrieved_success);
	        resp.setData(response);

	        return ResponseEntity.ok(resp);
	    } catch (Exception e) {
	        resp.setStatus(CommonMessages.FAILED);
	        resp.setMessage(CommonMessages.Error_fetch_Count );
	        return ResponseEntity.internalServerError().body(resp);
	    }
	}


}
