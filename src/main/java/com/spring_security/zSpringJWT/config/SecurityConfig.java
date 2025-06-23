package com.spring_security.zSpringJWT.config;

import java.util.Collections;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import com.spring_security.zSpringJWT.jwt.JWTFilter;
import com.spring_security.zSpringJWT.jwt.JWTUtil;
import com.spring_security.zSpringJWT.jwt.LoginFilter;

import jakarta.servlet.http.HttpServletRequest;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    //2. AuthenticationManager가 인자로 받을 AuthenticationConfiguraion 객체 생성자 주입
    private final AuthenticationConfiguration authenticationConfiguration;
    
	//JWTUtil 주입
	private final JWTUtil jwtutil; 

    public SecurityConfig(AuthenticationConfiguration authenticationConfiguration, JWTUtil jwtutil) {
        this.authenticationConfiguration = authenticationConfiguration;
        this.jwtutil = jwtutil;
    }

    //2. AuthenticationManager Bean 등록 -> LoginFilter에 인자를 전달하기 위해
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
	
	
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
	
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

		//csrf disable
		http.csrf((auth) -> auth.disable());
		
		//Form 로그인 방식 disable -> 커스텀해서 필터를 등록 addFilterAt
		http.formLogin((auth) -> auth.disable());
		
		//http basic 인증 방식 disable
		http.httpBasic((auth) -> auth.disable());
		
		//경로별 인가 작업
		http.authorizeHttpRequests(
			(auth) -> auth.requestMatchers("/login", "/", "/join").permitAll()
						  .requestMatchers("/admin").hasRole("ADMIN")
		                  .anyRequest().authenticated()
		);
	
		
		//2. 필터 추가 LoginFilter()는 인자를 받음 (AuthenticationManager() 메소드에 authenticationConfiguration 객체를 넣어야 함) 따라서 등록 필요
        http.addFilterAt(new LoginFilter(authenticationManager(authenticationConfiguration), jwtutil), UsernamePasswordAuthenticationFilter.class);
		
        
        //JWTFilter 등록
        http.addFilterBefore(new JWTFilter(jwtutil), LoginFilter.class);

        
		//세션 설정 -> jwt에서는 STATELESS로 관리해야함
		http.sessionManagement((session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
		
		
		//cors 설정 -> cors 설정 클래스도 따로 만들어야 함
		http.cors(
			(corsCustomizer -> corsCustomizer.configurationSource(new CorsConfigurationSource() {
	            @Override
	            public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
	                CorsConfiguration configuration = new CorsConfiguration();
	                configuration.setAllowedOrigins(Collections.singletonList("http://localhost:3000"));
	                configuration.setAllowedMethods(Collections.singletonList("*"));
	                configuration.setAllowCredentials(true);
	                configuration.setAllowedHeaders(Collections.singletonList("*"));
	                configuration.setMaxAge(3600L);
					configuration.setExposedHeaders(Collections.singletonList("Authorization"));
	                return configuration;
	            }
            }))
		);
		
		
		return http.build();
	}
	
}
