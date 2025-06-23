package com.spring_security.zSpringJWT.controller;

import java.util.Collection;
import java.util.Iterator;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@ResponseBody
public class MainController {

    @GetMapping("/")
    public String mainP() {

    	//세션정보 넘겨주기 -> username -> SecurityContextHolder에서 꺼낸다.
        String name = SecurityContextHolder.getContext().getAuthentication().getName();

        //세션정보 넘겨주기 -> role -> SecurityContextHolder에서 꺼낸다.
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iter = authorities.iterator();
        GrantedAuthority auth = iter.next();
        String role = auth.getAuthority();
        
        return "Main Controller : "+name +", "+ role;
    }
	
	
}
