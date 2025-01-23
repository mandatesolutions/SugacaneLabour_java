package com.sugarcanelabour.serviceimpl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.sugarcanelabour.Repository.RoleRepository;
import com.sugarcanelabour.entity.Role;
import com.sugarcanelabour.service.RoleService;

@Service
public class RoleServiceImpl implements RoleService{
	
	 private RoleRepository roleRepository;
	 

	public RoleServiceImpl(RoleRepository roleRepository) {
		this.roleRepository = roleRepository;
	}

	@Override
	public List<Role> getAllRoles() {
		return roleRepository.findAll();
		
	}

	@Override
	public Role createRole(Role role) {
		return roleRepository.save(role);
	}

	@Override
	public Role getRoleById(Long id) {
		return roleRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Role not found"));
	}

	@Override
	public Role updateRole(Long id, Role role) {
		 Role existingRole = getRoleById(id);
	        existingRole.setRoleName(role.getRoleName());  // update the role name
	        return roleRepository.save(existingRole);
	}
	
	  @Override
	    public void deleteRole(Long id) {
	        Role existingRole = getRoleById(id);
	        roleRepository.delete(existingRole);
	    }

}
