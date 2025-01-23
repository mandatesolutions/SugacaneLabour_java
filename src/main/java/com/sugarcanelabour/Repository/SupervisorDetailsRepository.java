package com.sugarcanelabour.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sugarcanelabour.entity.SupervisorDetails;

@Repository
public interface SupervisorDetailsRepository extends JpaRepository<SupervisorDetails, Long>{

}
