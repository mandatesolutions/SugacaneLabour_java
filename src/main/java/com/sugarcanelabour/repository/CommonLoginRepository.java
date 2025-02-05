package com.sugarcanelabour.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sugarcanelabour.entity.CommonLogin;
import com.sugarcanelabour.entity.Role;

@Repository
public interface CommonLoginRepository extends JpaRepository<CommonLogin, Long> {

	Optional<CommonLogin> findByEmail(String email);

	CommonLogin findByMobileNo(String identifier);

	CommonLogin findByUserId(Long commonLoginId);

	@Query("SELECT c FROM CommonLogin c JOIN c.role r WHERE r.roleName = :roleName")
	List<CommonLogin> findByRoleName(@Param("roleName") String roleName);
//	CommonLogin findByUserEmail(String currentUsername);
}
