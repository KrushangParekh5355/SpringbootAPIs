package com.user.api.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig{
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
		
		 http.csrf(csrf -> csrf.disable())
         .authorizeHttpRequests(auth -> auth
             // Allow login and register endpoints without authentication
             .requestMatchers("/api/users/login", "/api/users/register", "/api/users/all", "/api/users/update/**", "/api/users/delete/**", "/api/users/deleteAll").permitAll()
             .requestMatchers("/api/users/update/**").hasRole("ADMIN") // Restrict DELETE & UPDATE
             // Any other request requires authentication
             .anyRequest().authenticated()
         )
         .formLogin(login -> login
             .loginPage("/login") // Custom login page (optional)
             .permitAll()
         )
         .logout(logout -> logout
             .permitAll()
         ).httpBasic();

		
		return http.build();
	}
}
