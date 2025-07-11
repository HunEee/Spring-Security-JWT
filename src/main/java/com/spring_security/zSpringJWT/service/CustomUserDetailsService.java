package com.spring_security.zSpringJWT.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.spring_security.zSpringJWT.dto.CustomUserDetails;
import com.spring_security.zSpringJWT.entity.UserEntity;
import com.spring_security.zSpringJWT.repository.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    	//DB에서 조회	
        UserEntity userData = userRepository.findByUsername(username);
        
        System.out.println("CustomUserDetailsService -> 찾은 유저: " + userData);
        
        if (userData != null) {			
			//UserDetails에 담아서 return하면 AutneticationManager가 검증 함
            return new CustomUserDetails(userData);
        }
        
        return null;
    }	
	
}
