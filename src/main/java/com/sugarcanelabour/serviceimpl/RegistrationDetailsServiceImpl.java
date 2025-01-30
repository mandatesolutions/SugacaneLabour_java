//package com.sugarcanelabour.serviceimpl;
//
//import java.util.Optional;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import com.sugarcanelabour.entity.RegistrationDetails;
//import com.sugarcanelabour.model.RegistrationDto;
//;
//
//@Service
//public class RegistrationDetailsServiceImpl implements RegistrationDetailsService{
//	 @Autowired
//	    private RegistrationDetailsRepository registrationRepository;
//
//	 @Override
//	 public RegistrationDto getDetails(Long userId) {
//	     Optional<RegistrationDto> registration = registrationRepository.findByUserId(userId);
//	     if (!registration.isPresent()) {
//	         return null;
//	     }
//
//	     // Accessing the RegistrationDto object inside the Optional
//	     RegistrationDto registrationDto = registration.get();
//	     registrationDto.setFirstName(registrationDto.getFirstName());
//	     registrationDto.setLastName(registrationDto.getLastName());
//	     registrationDto.setGender(registrationDto.getGender());
//	     registrationDto.setBloodGroup(registrationDto.getBloodGroup());
//	     registrationDto.setAddress(registrationDto.getAddress());
//	     registrationDto.setRoleId(registrationDto.getRoleId());
//	     registrationDto.setDistrictId(registrationDto.getDistrictId());
//	     registrationDto.setTalukaId(registrationDto.getTalukaId());
//
//	     return registrationDto;
//	 }
//

