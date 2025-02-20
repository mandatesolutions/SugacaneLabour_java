package com.sugarcanelabour.service;

import java.util.Map;

import org.springframework.http.ResponseEntity;

import com.sugarcanelabour.helper.ApiResponse;

public interface AdminService {

	ResponseEntity<ApiResponse<Map<String, Object>>> getAllSupervisors();

	ResponseEntity<ApiResponse<Map<String, Object>>> getAllCoworkers();

	ResponseEntity<ApiResponse<Map<String, Object>>> getAllLabours();

	ResponseEntity<ApiResponse<Map<String, Object>>> getSupervisorById(Long userId);

	ResponseEntity<ApiResponse<Map<String, Object>>> getCoworkerById(Long commonLoginId);

	ResponseEntity<ApiResponse<Map<String, Object>>> getLabourById(Long commonLoginId);

}
