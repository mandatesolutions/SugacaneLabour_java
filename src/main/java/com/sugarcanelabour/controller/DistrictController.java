package com.sugarcanelabour.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import com.sugarcanelabour.Repository.DistrictRepository;
import com.sugarcanelabour.Repository.TalukaRepository;
import com.sugarcanelabour.entity.District;
import com.sugarcanelabour.entity.Taluka;

import lombok.extern.slf4j.Slf4j;


@RestController
@RequestMapping("/sclm/users/district")
@Slf4j
public class DistrictController {
	
	private DistrictRepository districtRepo;
	private TalukaRepository talukaRepo;
	

	public DistrictController(DistrictRepository districtRepo, TalukaRepository talukaRepo) {
	
		super();
		this.districtRepo = districtRepo;
		this.talukaRepo = talukaRepo;
		
	}

	@Transactional
	
	@GetMapping("/getAllDistricts")
	ResponseEntity<Object> getAllDistricts() {
		log.info("***** Inside - DistrictController - getAllDistricts *****");
		Map<Object, Object> response = new HashMap<>();
		
		List<District> data = districtRepo.findAll();
		response.put("getAllDistrictData", data);
		
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}


	@GetMapping("/getTalukas/{districtId}")
	ResponseEntity<Object> getAllTalukas(@PathVariable long districtId) {
		log.info("***** Inside - DistrictController - getAllTalukas *****");
		Map<Object, Object> response = new HashMap<>();
		List<Taluka> data = talukaRepo.findByDistrictDistrictId(districtId);
		response.put("getAllTalukasData", data);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}
}
