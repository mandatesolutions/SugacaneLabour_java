package com.sugarcanelabour.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sugarcanelabour.entity.District;

@Repository
public interface DistrictRepository extends JpaRepository<District, Long>{

}
