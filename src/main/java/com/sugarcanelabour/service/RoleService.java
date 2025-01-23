package com.sugarcanelabour.service;

import java.util.List;

import com.sugarcanelabour.entity.Role;

public interface RoleService {

	List<Role> getAllRoles();

	Role createRole(Role role);

	Role getRoleById(Long id);
	
	Role updateRole(Long id, Role role);

	void deleteRole(Long id);
	
}
