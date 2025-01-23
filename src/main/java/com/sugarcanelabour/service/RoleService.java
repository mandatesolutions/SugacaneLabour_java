package com.sugarcanelabour.service;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.sugarcanelabour.entity.Role;

public interface RoleService {

	ResponseEntity<Object> getAllRoles();


	ResponseEntity<Object> getRoleById(Long id);
	
	ResponseEntity<Object> updateRole(Long id, Role role);

	ResponseEntity<Object> deleteRole(Long id);

	ResponseEntity<Object> addRole(Role role);
	
}
