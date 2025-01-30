package com.sugarcanelabour.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sugarcanelabour.entity.CommonLogin;
import com.sugarcanelabour.entity.SupervisorDetails;

@Repository
public interface SupervisorDetailsRepository extends JpaRepository<SupervisorDetails, Long>{


	Optional<SupervisorDetails> findByCommonLogin(CommonLogin commonLogin);

}
