package com.sourceware.labs.idp.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.sourceware.labs.idp.entity.User;
import com.sourceware.labs.idp.repo.UserRepo;
import com.sourceware.labs.idp.service.AwsEmailService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Spring rest controller for managing user authentication information
 * <br />
 * Routes:
 * <ul>
 * <li>- POST /user/signup: Handle signing up a new user with the IDP server</li>
 * <li>- POST /user/login: Method: Handle logging in a user with the IDP server</li>
 * <li>- POST /user/verify: Method: Handle verifying a new user's account</li>
 * <li>- GET /user/recover/questions: Return the user's security questions</li>
 * <li>- POST /user/recover/questions: Verify that the user's security question answers are correct</li>
 * <li>- GET /user/recover/email: Send an email to the user's email or recoveryEmail that can allow them to recover their account</li>
 * <li>- POST /user/recover/email: Verify that the user's verification token obtained from email is valid</li>
 * <li>- GET /user/recover/phone: Send a verification code to the user's phone that can be used to recover their account</li>
 * <li>- POST /user/recover/email: Verify that the user's verification code obtained from phone is valid</li>
 * <li>- PUT /user/update: Updates the user's information</li>
 * </ul>
 * TODO finish working on the outlined routes
 * @author Robert Forristall (robert.s.forristall@gmail.com)
 */
@RestController
public class UserController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

	private final UserRepo repo;
	
	@Autowired
	private AwsEmailService awsEmailService;
	
	UserController(UserRepo repo) {
		this.repo = repo;
	}
	
	@Operation(summary = "Get all Users", description = "Get all users and return them as a JSON array")
	@Tag(name = "get", description = "GET methods for User APIs")
	@GetMapping("/user")
	List<User> all() {
		LOGGER.info("in GET /user ...");
		return repo.findAll();
	}
	
	@ApiResponses({
	       @ApiResponse(responseCode = "200", content = { @Content(mediaType = "application/json",
	               schema = @Schema(implementation = User.class)) }),
	       @ApiResponse(responseCode = "404", description = "User not found",
	               content = @Content) })
	@Tag(name = "get", description = "GET methods for User APIs")
	@GetMapping("/user/{userId}")
	User user(@Parameter(
		       description = "ID of user to be retrieved",
		       required = true)
		@PathVariable long userId) {
		LOGGER.info("in GET /user single ...");
		return repo.findById(userId).orElseThrow(() -> new RuntimeException("User id not found: " + userId));
	}
	
	@Tag(name = "post", description = "POST method for adding a new user")
	@PostMapping("user")
	User newUser(@RequestBody User newUser, HttpServletResponse response) {
		LOGGER.info("in POST /user ...");
		User user = repo.save(newUser);
		response.setStatus(HttpStatus.CREATED.value());
		return user;
	}
}
