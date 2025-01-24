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
import com.sugarcanelabour.helper.ApiResponse;
import com.sugarcanelabour.helper.CommonMessages;
import com.sugarcanelabour.repository.RoleRepository;
import com.sugarcanelabour.service.RoleService;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class RoleServiceImpl implements RoleService {

	private RoleRepository roleRepository;

	public RoleServiceImpl(RoleRepository roleRepository) {
		this.roleRepository = roleRepository;
	}

	 private Map<Object, Object> response;

	// add role
	@Transactional
	@Override
	public ResponseEntity<ApiResponse<String>> addRole(Role role) {
		if (log.isInfoEnabled()) {
			log.info("***** Inside RoleServiceImpl - addRole *****");
		}
		Optional<Role> existingRole = roleRepository.findByRoleName(role.getRoleName());
		ApiResponse<String> response = new ApiResponse<>();

		if (existingRole.isPresent()) {
			response.setStatus(CommonMessages.FAILED);
			response.setMessage("Role with the name " + role.getRoleName() + " already exists.");
			return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
		}

		roleRepository.save(role);
		response.setStatus(CommonMessages.SUCCESS);
		response.setMessage(CommonMessages.ROLE_ADD_SUCCESSFUL);
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	// get All roles

	@Transactional
	@Override
	public ResponseEntity<ApiResponse<List<Role>>> getAllRoles() {
		if (log.isInfoEnabled()) {
			log.info("***** Inside RoleServiceImpl - getAllRoles *****");
		}
		List<Role> roles = roleRepository.findAll();
		if (roles.isEmpty()) {
			throw new ResourceNotFoundException("No roles available.");
		}
		ApiResponse<List<Role>> response = new ApiResponse<>(CommonMessages.SUCCESS, CommonMessages.ROLE_GET_SUCCESSFUL,
				roles);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	// get role by id
	@Transactional
	@Override
	public ResponseEntity<ApiResponse<Role>> getRoleById(Long id) {
		if (log.isInfoEnabled()) {
			log.info("***** Inside RoleServiceImpl - getRoleById *****");
		}
		Role role = roleRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Role with ID " + id + " not found."));
		ApiResponse<Role> response = new ApiResponse<>(role);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	// update Role

	@Transactional
	@Override
	public ResponseEntity<Object> updateRole(Long id, Role updatedRole) {
		response = new HashMap<>();
		Role existingRole = roleRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Role with ID " + id + " not found."));
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

		Role existingRole = roleRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Role with ID " + id + " not found."));
		roleRepository.delete(existingRole);
		response.put("status", "success");
		response.put("message", CommonMessages.ROLE_DELETE_SUCCESSFUL);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

}
