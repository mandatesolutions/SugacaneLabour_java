package com.sugarcanelabour.serviceimpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.sugarcanelabour.entity.CommonLogin;
import com.sugarcanelabour.entity.Document;
import com.sugarcanelabour.entity.SupervisorDetails;
import com.sugarcanelabour.helper.ApiResponse;
import com.sugarcanelabour.repository.CommonLoginRepository;
import com.sugarcanelabour.repository.DocumentRepository;
import com.sugarcanelabour.repository.SupervisorDetailsRepository;
import com.sugarcanelabour.service.LaborService;

@Service
public class LaborServiceImpl implements LaborService{
	
	
	 private  SupervisorDetailsRepository supervisorDetailsRepository;
	    private  DocumentRepository documentRepository;
	    private  CommonLoginRepository commonLoginRepository;

	  

	    public LaborServiceImpl(SupervisorDetailsRepository supervisorDetailsRepository,
				DocumentRepository documentRepository, CommonLoginRepository commonLoginRepository) {
			this.supervisorDetailsRepository = supervisorDetailsRepository;
			this.documentRepository = documentRepository;
			this.commonLoginRepository = commonLoginRepository;
		}



		public ResponseEntity<ApiResponse<Map<String, Object>>> getLaborDetails(Long commonLoginId) {
	        ApiResponse<Map<String, Object>> response = new ApiResponse<>();
	        Map<String, Object> data = new HashMap<>();

	        // Fetch labor details
	        CommonLogin commonLogin = commonLoginRepository.findById(commonLoginId)
	                .orElseThrow(() -> new RuntimeException("Labor not found"));

	        // Fetch supervisor details (assuming a supervisor or labor-related entity exists)
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
	        data.put("firstName", supervisorDetails.getFirstName());
	        data.put("lastName", supervisorDetails.getLastName());
	        data.put("gender", supervisorDetails.getGender());
	        data.put("bloodGroup", supervisorDetails.getBloodGroup());
	        data.put("address", supervisorDetails.getAddress());
	        data.put("districtId", supervisorDetails.getDistrictId());
	        data.put("talukaId", supervisorDetails.getTalukaId());

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

	        response.setStatus("SUCCESS");
	        response.setMessage("Labor details fetched successfully.");
	        response.setData(data);

	        return new ResponseEntity<>(response, HttpStatus.OK);
	    }

}
