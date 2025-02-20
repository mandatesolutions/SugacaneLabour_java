package com.sugarcanelabour.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sugarcanelabour.entity.CommonLogin;
import com.sugarcanelabour.entity.Role;
import com.sugarcanelabour.model.LaboursDto;

@Repository
public interface CommonLoginRepository extends JpaRepository<CommonLogin, Long> {

	Optional<CommonLogin> findByEmail(String email);

	CommonLogin findByMobileNo(String identifier);

	CommonLogin findByUserId(Long commonLoginId);

	@Query("SELECT c FROM CommonLogin c JOIN c.role r WHERE r.roleName = :roleName")
	List<CommonLogin> findByRoleName(@Param("roleName") String roleName);
//	CommonLogin findByUserEmail(String currentUsername);

	List<CommonLogin> findByRole_RoleName(String string);

	 long countByRoleRoleName(String roleName);

	long countByRoleId(Long roleId);

	@Query("SELECT COALESCE(MONTH(cl.createdAt), 1), r.roleName, COUNT(DISTINCT cl.userId) " +
		       "FROM CommonLogin cl " +
		       "JOIN Role r ON cl.role.id = r.id " +
		       "WHERE r.roleName != 'ROLE_SUP-ADMIN' " +  
		       "GROUP BY MONTH(cl.createdAt), r.roleName " +
		       "ORDER BY MONTH(cl.createdAt) DESC")
		List<Object[]> findRegistrationCountByMonthAndRole();



	Long countByRole_RoleName(String roleName);

	  @Query("SELECT new com.sugarcanelabour.model.LaboursDto(cl.uuid, sd.profileImage, sd.firstName, sd.lastName, cl.email) " +
	           "FROM CommonLogin cl " +
	           "JOIN SupervisorDetails sd ON cl = sd.commonLogin " +
	           "JOIN Role r ON cl.role.id = r.id " +
	           "WHERE r.roleName = 'ROLE_LABOUR' " +
	           "ORDER BY cl.createdAt DESC")
	    Page<LaboursDto> findLatestLabors(Pageable pageable);

	  @Query("SELECT COALESCE(MONTH(cl.createdAt), 1), r.roleName, COUNT(DISTINCT cl.userId) " +
		       "FROM CommonLogin cl " +
		       "JOIN Role r ON cl.role.id = r.id " +
		       "WHERE r.roleName != 'ROLE_SUP-ADMIN' AND r.roleName != 'ROLE_ADMIN' " +  // Exclude both ROLE_SUP-ADMIN and ROLE_ADMIN
		       "GROUP BY MONTH(cl.createdAt), r.roleName " +
		       "ORDER BY MONTH(cl.createdAt) DESC")
		List<Object[]> findCountByMonthAndRole();
		
		
		 @Query("SELECT COALESCE(MONTH(cl.createdAt), 1), r.roleName, COUNT(DISTINCT cl.userId) " +
			       "FROM CommonLogin cl " +
			       "JOIN Role r ON cl.role.id = r.id " +
			       "WHERE r.roleName != 'ROLE_SUP-ADMIN' AND r.roleName != 'ROLE_ADMIN' AND r.roleName != 'ROLE_SUPERVISOR' " +  // Exclude both ROLE_SUP-ADMIN and ROLE_ADMIN
			       "GROUP BY MONTH(cl.createdAt), r.roleName " +
			       "ORDER BY MONTH(cl.createdAt) DESC")
			List<Object[]> findCountByMonth_Role();
			
			 @Query("SELECT COALESCE(MONTH(cl.createdAt), 1), r.roleName, COUNT(DISTINCT cl.userId) " +
				       "FROM CommonLogin cl " +
				       "JOIN Role r ON cl.role.id = r.id " +
				       "WHERE r.roleName != 'ROLE_SUP-ADMIN' AND r.roleName != 'ROLE_ADMIN' AND r.roleName != 'ROLE_SUPERVISOR' AND r.roleName != 'ROLE_COWORKER'" +  // Exclude both ROLE_SUP-ADMIN and ROLE_ADMIN
				       "GROUP BY MONTH(cl.createdAt), r.roleName " +
				       "ORDER BY MONTH(cl.createdAt) DESC")
				List<Object[]> findCountBy_Month_Role();

		

				@Query(value = "SELECT r.role_name, COUNT(*) " +
			               "FROM common_login cl " +
			               "JOIN role r ON cl.role_id = r.id " +
			               "WHERE DATE(cl.created_at) = CURRENT_DATE " +
			               "GROUP BY r.role_name", nativeQuery = true)
			List<Object[]> findTodayRegistrationCount();




}
