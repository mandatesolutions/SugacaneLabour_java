package com.sugarcanelabour.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sugarcanelabour.entity.Role;
import com.sugarcanelabour.service.RoleService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/sclm/labor")
public class RoleController {
	
	private RoleService roleService;
	
	 public RoleController(RoleService roleService) {
		this.roleService = roleService;
	}


	 @Operation(summary = "Add Role Api", description = "This API is used to add the roles")
	@PostMapping("/add")
	    public ResponseEntity<Object> addRole(@RequestBody Role role) 
	{
		if (log.isInfoEnabled())
		{
			log.info("***** Inside RoleController - add *****");
		}
		return roleService.addRole(role);
	}

	 //get all roles
	 	@Operation(summary = "Get All Roles API", description = "This API is used to retrieve all roles")
	    @GetMapping("/all")
	    public ResponseEntity<Object> getAllRoles() {
	        if (log.isInfoEnabled()) {
	            log.info("***** Inside RoleController - getAllRoles *****");
	        }
	        return roleService.getAllRoles();
	    }
	 
	 	//get role by id
	 	 @Operation(summary = "Get Role by ID API", description = "This API is used to retrieve a role by its ID")
	     @GetMapping("/{id}")
	     public ResponseEntity<Object> getRoleById(@PathVariable Long id) {
	         if (log.isInfoEnabled()) {
	             log.info("***** Inside RoleController - getRoleById *****");
	         }
	         return roleService.getRoleById(id);
	        
	     }
	 	 
	 	 //update role
	 	 @Operation(summary = "Update Role API", description = "This API is used to update an existing role")
	     @PutMapping("/update/{id}")
	     public ResponseEntity<Object> updateRole(@PathVariable Long id, @Valid @RequestBody Role role) {
	         if (log.isInfoEnabled()) {
	             log.info("***** Inside RoleController - updateRole *****");
	         }
	         return roleService.updateRole(id, role);
	         
	     }
    
    // Delete a role by ID
	 	@Operation(summary = "Delete Role API", description = "This API is used to delete a role by its ID")
	    @DeleteMapping("/delete/{id}")
	    public ResponseEntity<Object> deleteRole(@PathVariable Long id) {
	        if (log.isInfoEnabled()) {
	            log.info("***** Inside RoleController - deleteRole *****");
	        }
	        return roleService.deleteRole(id);
	 	}
}
