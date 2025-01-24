package com.sugarcanelabour.service;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.sugarcanelabour.entity.Role;
import com.sugarcanelabour.helper.ApiResponse;

public interface RoleService {

	ResponseEntity<ApiResponse<List<Role>>> getAllRoles();


	ResponseEntity<ApiResponse<Role>> getRoleById(Long id);
	
	ResponseEntity<Object> updateRole(Long id, Role role);

	ResponseEntity<Object> deleteRole(Long id);

	ResponseEntity<ApiResponse<String>> addRole(Role role);
	
}
