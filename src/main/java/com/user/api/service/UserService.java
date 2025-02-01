package com.user.api.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
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
	
	//Getting list of all users
	public List<User> getAllUsers(){
		return userRepository.findAll();
	}
	
	
	//Adding user
	@Transactional
	public User registeUser(User user) {
		Optional<User> existingUser = userRepository.findByEmail(user.getEmail());
		
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
	
	
	
	
	
	
	
	
}
