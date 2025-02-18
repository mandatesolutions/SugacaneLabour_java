package com.sugarcanelabour.service;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;

import com.sugarcanelabour.helper.ApiResponse;
import com.sugarcanelabour.model.LaboursDto;

public interface CoworkerService {

	ResponseEntity<ApiResponse<Map<String, Object>>> getCountByAllRoles();

	ResponseEntity<ApiResponse<Map<String, Object>>> getCountByMonth();

	ResponseEntity<ApiResponse<List<LaboursDto>>> getLatestLaborDetails();

	ResponseEntity<ApiResponse<Map<String, Object>>> getAllLabours();

}
