package com.sugarcanelabour.serviceimpl;

import org.springframework.stereotype.Service;

import com.sugarcanelabour.Repository.CommonLoginRepository;
import com.sugarcanelabour.entity.CommonLogin;
import com.sugarcanelabour.service.CommonLoginService;

@Service
public class CommonLoginServiceImpl implements CommonLoginService {
	
	  private CommonLoginRepository loginRepository;

	 @Override
	    public String login(String email, String password) {
	        CommonLogin user = loginRepository.findByEmail(email)
	                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

	        if (!user.getPassword().equals(password)) {
	            throw new IllegalArgumentException("Invalid email or password");
	        }

	        return "Login successful for role: " + user.getRole();
	    }

}
