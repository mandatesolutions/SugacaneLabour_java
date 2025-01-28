package com.sugarcanelabour.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import com.sugarcanelabour.entity.CommonLogin;

import com.sugarcanelabour.config.CustomJwtUserDetail;
import com.sugarcanelabour.repository.CommonLoginRepository;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class CustomJwtUserDetailService implements UserDetailsService{

	@Autowired
	private CommonLoginRepository commonLoginRepository;
	@Transactional
	 @Override
	    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
	        if (log.isInfoEnabled()) {
	            log.info("***** Inside CustomJwtUserDetailService - loadUserByUsername *****");
	        }

	        // Fetch the CommonLogin by email
	        CommonLogin commonLogin = commonLoginRepository.findByEmail(identifier)
	                .orElseThrow(() -> new UsernameNotFoundException("User not found with identifier: " + identifier));

	        
	      //   If no user found by email, try searching by mobile number or password
	        if (commonLogin == null) {
	            commonLogin = commonLoginRepository.findByMobileNo(identifier);
	        }
	        
	        // If user is still not found, throw exception
	        if (commonLogin == null) {
	            throw new UsernameNotFoundException("User not found with identifier: " + identifier);
	        }

	        // Wrap the CommonLogin entity in a CustomJwtUserDetail object
	        CustomJwtUserDetail customJwtUserDetail = new CustomJwtUserDetail(commonLogin);
	        return customJwtUserDetail;
	    }

}
