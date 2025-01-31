package com.sugarcanelabour.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.sugarcanelabour.entity.CommonLogin;
import com.sugarcanelabour.exception.AuthenticationException;
import com.sugarcanelabour.repository.CommonLoginRepository;

import java.util.Optional;

@Service
public class AccessControlService {
    
    @Autowired
    private CommonLoginRepository loginRepository;

    public void verifyUserAccess(Long requestedUserId) {
        // Extract the currently authenticated user's username
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        
        // Retrieve the current user (Optional type)
        Optional<CommonLogin> currentUser = loginRepository.findByEmail(currentUsername);

        // Check if user is present and verify userId
        if (currentUser.isEmpty() || !Long.valueOf(currentUser.get().getUserId()).equals(requestedUserId)) {
            throw new AuthenticationException("You are not authorized to access this resource",null);
        }
    }
}
