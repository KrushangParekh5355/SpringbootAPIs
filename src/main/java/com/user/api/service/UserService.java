package com.user.api.service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.user.api.entity.User;
import com.user.api.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserService {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	//For password encryption
	public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
	}
	
	//Getting list of all users
	public List<User> getAllUsers(){
		return userRepository.findAll();
	}
	
	//Adding user
	@Transactional
	public User registerUser(User user, boolean isAdmin) {
		Optional<User> existingUser = userRepository.findByEmail(user.getEmail());
		user.setPassword(passwordEncoder.encode(user.getPassword())); //Password encryption
			
		Set<String> roles = new HashSet<>();
		if(isAdmin) {
			roles.add("ADMIN");
		}else {
			roles.add("USER");
		}
		user.setRoles(roles);
		
		if(existingUser.isPresent()) {
			throw new RuntimeException("Email is already registered!");
		}
		log.info("Saving user: {}", user.getEmail());
		return userRepository.save(user);
	}
	
	
	//Updating existing user's details
	public User updateUser(Long userId, User updatedUser) {
		Optional<User> existingUser = userRepository.findById(userId);
		
		if(existingUser.isPresent()) {
			User user = existingUser.get();
			user.setName(updatedUser.getName());
			user.setEmail(updatedUser.getEmail());
			user.setPassword(updatedUser.getPassword());
			
			log.info("User updated with email: {} to this email: ", user.getEmail(), updatedUser.getEmail());
			return userRepository.save(user);
		} else {
			throw new RuntimeException("User not found with ID: " + userId);
		}
	}
	
	//Deleting user
	public void deleteUser(Long userId) {
		if(userRepository.existsById(userId)) {
			userRepository.deleteById(userId);
		} else {
			log.info("User not found with ID: {}", userId);
			throw new RuntimeException("User not found with ID: "+ userId);			
		}
	}
	
	//Deleting all users
	public void deleteAllUsers() {
		userRepository.deleteAll();
		jdbcTemplate.execute("ALTER TABLE users AUTO_INCREMENT = 1");
	}
	
	//Login user 
	public User loginUser(String email, String password) {
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new RuntimeException("User not found!"));

		if(!passwordEncoder.matches(password, user.getPassword())) {
			throw new RuntimeException("Invalid credentials");
		}
		
		log.info("User Logged in successfully!");
		return user;
	}
	
	
	
	
	
	
}
