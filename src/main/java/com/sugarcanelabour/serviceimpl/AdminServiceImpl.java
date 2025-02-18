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
import com.sugarcanelabour.repository.CommonLoginRepository;
import com.sugarcanelabour.repository.DocumentRepository;
import com.sugarcanelabour.repository.SupervisorDetailsRepository;
import com.sugarcanelabour.service.AdminService;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AdminServiceImpl implements AdminService{
	
	
	@Autowired
	private CommonLoginRepository loginRepository;

	@Autowired
	private SupervisorDetailsRepository supervisorDetailsRepository;

	@Autowired
	private DocumentRepository documentRepository;

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
