package com.sugarcanelabour.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sugarcanelabour.entity.CommonLogin;
import com.sugarcanelabour.entity.Role;

@Repository
public interface CommonLoginRepository extends JpaRepository<CommonLogin, Long>{

	 
	  Optional<CommonLogin> findByEmail(String email);

	CommonLogin findByMobileNo(String identifier);
}
