package com.sugarcanelabour.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sugarcanelabour.entity.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long>{
	
	 Role findByRoleName(String roleName);

}
