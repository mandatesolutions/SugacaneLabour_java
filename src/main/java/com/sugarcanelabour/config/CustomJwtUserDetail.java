package com.sugarcanelabour.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.sugarcanelabour.entity.CommonLogin;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CustomJwtUserDetail implements UserDetails{
	
	private CommonLogin commonLogin;

	 @Override
	    public Collection<? extends GrantedAuthority> getAuthorities() {
	        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
	        System.out.println("role"+commonLogin.getRole().getRoleName());
	        authorities.add(new SimpleGrantedAuthority(commonLogin.getRole().getRoleName()));
	        return authorities;
	    }

	 @Override
	    public String getPassword() {
	        return commonLogin.getPassword();
	    }

	    @Override
	    public String getUsername() {
	        return commonLogin.getEmail();
	    }

}
