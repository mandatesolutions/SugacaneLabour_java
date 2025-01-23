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

import io.swagger.v3.oas.annotations.parameters.RequestBody;

@RestController
@RequestMapping("/api/roles")
public class RoleController {
	
	private RoleService roleService;
	
	 public RoleController(RoleService roleService) {
		this.roleService = roleService;
	}


	@PostMapping("/create")
	    public ResponseEntity<Role> createRole(@RequestBody Role role) {
	        Role createdRole = roleService.createRole(role);
	        return new ResponseEntity<>(createdRole, HttpStatus.CREATED);
	    }

	 // Get all roles
    @GetMapping("/getAll")
    public ResponseEntity<List<Role>> getAllRoles() {
        List<Role> roles = roleService.getAllRoles();
        return new ResponseEntity<>(roles, HttpStatus.OK);
    }
    
    //get role by id
    @GetMapping("/{id}")
    public ResponseEntity<Role> getRoleById(@PathVariable Long id) {
        Role role = roleService.getRoleById(id);
        return new ResponseEntity<>(role, HttpStatus.OK);
    }
    
    //update role by id
    @PutMapping("/update/{id}")
    public ResponseEntity<Role> updateRole(@PathVariable Long id, @RequestBody Role role)
    {
    	Role updatedRole = roleService.updateRole(id,role);
    	return new ResponseEntity<>(updatedRole , HttpStatus.OK);
    }
    
    // Delete a role by ID
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteRole(@PathVariable Long id) {
        roleService.deleteRole(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    
}
