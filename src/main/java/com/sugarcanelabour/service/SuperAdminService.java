package com.sugarcanelabour.service;

import java.util.Map;

import org.springframework.http.ResponseEntity;

import com.sugarcanelabour.helper.ApiResponse;

public interface SuperAdminService {

	ResponseEntity<ApiResponse<Map<String, Object>>> getAdminDetails(Long userId);

	ResponseEntity<ApiResponse<Map<String, Object>>> getSupervisorDetails(Long userId);

	ResponseEntity<ApiResponse<Map<String, Object>>> getCoworkerDetails(Long userId);

	ResponseEntity<ApiResponse<Map<String, Object>>> getLaborDetails(Long userId);

	ResponseEntity<ApiResponse<Map<String, Object>>> deactivateUser(Long userId);

	ResponseEntity<ApiResponse<Map<String, Object>>> getAllAdminDetails();

	ResponseEntity<ApiResponse<Map<String, Object>>> getAllSupervisors();

	ResponseEntity<ApiResponse<Map<String, Object>>> getAllCoworkers();

	ResponseEntity<ApiResponse<Map<String, Object>>> getAllLabours();

}
