package com.sugarcanelabour.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sugarcanelabour.entity.Taluka;

@Repository
public interface TalukaRepository extends JpaRepository<Taluka, Long>{

	List<Taluka> findByDistrictDistrictId(long districtId);

}
