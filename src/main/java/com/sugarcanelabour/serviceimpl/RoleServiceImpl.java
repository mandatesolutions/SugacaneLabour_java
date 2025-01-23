package com.sugarcanelabour.serviceimpl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.sugarcanelabour.entity.Role;
import com.sugarcanelabour.exception.ResourceNotFoundException;
import com.sugarcanelabour.helper.CommonMessages;
import com.sugarcanelabour.repository.RoleRepository;
import com.sugarcanelabour.service.RoleService;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class RoleServiceImpl implements RoleService{
	
	 private RoleRepository roleRepository;
	 

	public RoleServiceImpl(RoleRepository roleRepository) {
		this.roleRepository = roleRepository;
	}
	 private Map<Object, Object> response;
	 
	 //add role
	 	@Transactional
	    @Override
	    public ResponseEntity<Object> addRole(Role role) {
	        response = new HashMap<>();
	        
	        // Check if the role already exists
	        Optional<Role> existingRole = roleRepository.findByRoleName(role.getRoleName());
	        if (existingRole.isPresent()) {
	            response.put("status", "failure");
	            response.put("message", "Role with the name " + role.getRoleName() + " already exists.");
	            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	        }

	        roleRepository.save(role);
	        response.put("status", "success");
	        response.put("message", CommonMessages.ROLE_ADD_SUCCESSFUL);
	        return new ResponseEntity<>(response, HttpStatus.CREATED);
	    }
	 
	 //get All roles

	 	@Transactional
	    @Override
	    public ResponseEntity<Object> getAllRoles() {
	        response = new HashMap<>();
	        
	        List<Role> roles = roleRepository.findAll();
	        if (roles.isEmpty()) {
	            throw new ResourceNotFoundException("No roles available.");
	        }

	        response.put("roles", roles);
	        return new ResponseEntity<>(response, HttpStatus.OK);
	    }

	 //get role by id
	 @Transactional
	    @Override
	    public ResponseEntity<Object> getRoleById(Long id) {
	        response = new HashMap<>();
	        
	        Role role = roleRepository.findById(id).orElseThrow(() -> 
	            new ResourceNotFoundException("Role with ID " + id + " not found."));
	        
	        response.put("role", role);
	        return new ResponseEntity<>(response, HttpStatus.OK);
	    }

	 //update Role
	 
	 @Transactional
	    @Override
	    public ResponseEntity<Object> updateRole(Long id, Role updatedRole) {
	        response = new HashMap<>();
	        
	        Role existingRole = roleRepository.findById(id).orElseThrow(() -> 
	            new ResourceNotFoundException("Role with ID " + id + " not found."));
	        
	        existingRole.setRoleName(updatedRole.getRoleName());

	        roleRepository.save(existingRole);

	        response.put("status", "success");
	        response.put("message", CommonMessages.ROLE_UPDATE_SUCCESSFUL);
	        return new ResponseEntity<>(response, HttpStatus.OK);
	    }

	 // Delete role
	    @Transactional
	    @Override
	    public ResponseEntity<Object> deleteRole(Long id) {
	        response = new HashMap<>();

	        Role existingRole = roleRepository.findById(id).orElseThrow(() -> 
	            new ResourceNotFoundException("Role with ID " + id + " not found."));
	        
	        roleRepository.delete(existingRole);

	        response.put("status", "success");
	        response.put("message", CommonMessages.ROLE_DELETE_SUCCESSFUL);
	        return new ResponseEntity<>(response, HttpStatus.OK);
	    }
	


}
