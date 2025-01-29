package com.sugarcanelabour.serviceimpl;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.cache.annotation.CacheEvict;
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
		ApiResponse<String> response = new ApiResponse<>();
		Optional<Role> existingRole = roleRepository.findByRoleName(role.getRoleName());

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

	// @Cacheable(value = "roles", key = "'allRoles'")
	@Transactional
	@Override
	public ApiResponse<List<Role>> getAllRoles() {
		if (log.isInfoEnabled()) {
			log.info("***** Inside RoleServiceImpl - getAllRoles *****");
		}
		List<Role> roles = roleRepository.findAll();
		if (roles.isEmpty()) {
			throw new ResourceNotFoundException("No roles available.");
		}
		return new ApiResponse<>(CommonMessages.SUCCESS, CommonMessages.ROLE_GET_SUCCESSFUL, roles);

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

	@CacheEvict(value = "roles", allEntries = true)
	@Transactional
	@Override
	public ResponseEntity<ApiResponse<String>> updateRole(Long id, Role updatedRole) {
		if (log.isInfoEnabled()) {
			log.info("***** Inside RoleServiceImpl - updateRole *****");
		}
		Role existingRole = roleRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Role with ID " + id + " not found."));
		existingRole.setRoleName(updatedRole.getRoleName());
		roleRepository.save(existingRole);
		ApiResponse<String> response = new ApiResponse<String>(CommonMessages.SUCCESS,
				CommonMessages.ROLE_UPDATE_SUCCESSFUL, null);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	// Delete role
	@CacheEvict(value = "roles", allEntries = true)
	@Transactional
	@Override
	public ResponseEntity<ApiResponse<String>> deleteRole(Long id) {
		if (log.isInfoEnabled()) {
			log.info("***** Inside RoleServiceImpl - deleteRole *****");
		}
		Role existingRole = roleRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Role with ID " + id + " not found."));
		roleRepository.delete(existingRole);
		ApiResponse<String> response = new ApiResponse<>();
		response.setStatus(CommonMessages.SUCCESS);
		response.setMessage(CommonMessages.ROLE_DELETE_SUCCESSFUL);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

}
