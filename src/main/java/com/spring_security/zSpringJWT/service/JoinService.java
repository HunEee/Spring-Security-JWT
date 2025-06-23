package com.spring_security.zSpringJWT.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.spring_security.zSpringJWT.dto.JoinDTO;
import com.spring_security.zSpringJWT.entity.UserEntity;
import com.spring_security.zSpringJWT.repository.UserRepository;

@Service
public class JoinService {

	private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public JoinService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public boolean joinProcess(JoinDTO joinDTO) {

        String username = joinDTO.getUsername();
        String password = joinDTO.getPassword();

        // 이미 유저가 존재하는 확인하는 로직
        Boolean isExist = userRepository.existsByUsername(username);
        if (isExist) {
            return false;
        }

        UserEntity data = new UserEntity();
        data.setUsername(username);
        data.setPassword(bCryptPasswordEncoder.encode(password));
        data.setRole("ROLE_ADMIN");

        userRepository.save(data);
		return isExist;
    }
	
}
