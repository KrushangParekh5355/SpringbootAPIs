package com.user.api.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.user.api.entity.User;
import com.user.api.service.UserService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/users")
@Slf4j
public class UserController {
	@Autowired
	private UserService userService;
	
	@GetMapping("/all")
	public ResponseEntity<List<User>> getAllUsers(){
		List<User> users = userService.getAllUsers();
		log.info("Listed all the users");
		return ResponseEntity.ok(users);
	}
	
	@PostMapping("/register")
	public ResponseEntity<String> registerUser(@RequestBody User user, @RequestParam(defaultValue = "false") boolean isAdmin){
		try {
			userService.registerUser(user, isAdmin);
			log.info("Created new users");
			return ResponseEntity.ok("User is registered successfully!");
		} catch(Exception e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	
	@PreAuthorize("hasrole('ADMIN')")
	@PutMapping("/update/{id}")
	public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody User updatedUser){
		try {
			User user = userService.updateUser(id, updatedUser);
			log.info("Updated the user: {}", updatedUser);
			return ResponseEntity.ok(user);
		}catch(RuntimeException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	
	@PreAuthorize("hasrole('ADMIN')")
	@DeleteMapping("delete/{id}")
	public ResponseEntity<String> deleteUser(@PathVariable Long id){
		try {
			userService.deleteUser(id);
			log.info("Deleted the user with ID: {}", id);
			return ResponseEntity.ok("User deleted successfully");
		} catch (RuntimeException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	
	@PreAuthorize("hasrole('ADMIN')")
	@DeleteMapping("deleteAll")
	 public ResponseEntity<String> deleteAllUsers() {
        try {
            userService.deleteAllUsers();
            return ResponseEntity.ok("All users deleted");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error deleting users: " + e.getMessage());
        }
    } 
	
	@PostMapping("/login")
	public User login(@RequestBody User loginRequest) {
		return userService.loginUser(loginRequest.getEmail(), loginRequest.getPassword());
	}
	
	
}
