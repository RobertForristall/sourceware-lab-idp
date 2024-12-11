package com.sourceware.labs.idp.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.sourceware.labs.idp.entity.User;
import com.sourceware.labs.idp.repo.UserRepository;

import jakarta.servlet.http.HttpServletResponse;

@RestController
public class UserController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

	private final UserRepository repo;
	
	UserController(UserRepository repo) {
		this.repo = repo;
	}
	
	
	@GetMapping("/user")
	List<User> all() {
		LOGGER.info("in GET /user ...");
		return repo.findAll();
	}
	
	@PostMapping("user")
	User newUser(@RequestBody User newUser, HttpServletResponse response) {
		LOGGER.info("in POST /user ...");
		User user = repo.save(newUser);
		response.setStatus(HttpStatus.CREATED.value());
		return user;
	}
}
