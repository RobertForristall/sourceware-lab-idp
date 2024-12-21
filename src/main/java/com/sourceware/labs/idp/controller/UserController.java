package com.sourceware.labs.idp.controller;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Optional;
import java.util.Set;

import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sourceware.labs.idp.entity.AccountVerification;
import com.sourceware.labs.idp.entity.Role;
import com.sourceware.labs.idp.entity.SecurityQuestion;
import com.sourceware.labs.idp.entity.User;
import com.sourceware.labs.idp.entity.Role.Application;
import com.sourceware.labs.idp.entity.Role.RoleName;
import com.sourceware.labs.idp.repo.AccountVerificationRepo;
import com.sourceware.labs.idp.repo.RoleRepo;
import com.sourceware.labs.idp.repo.UserRepo;
import com.sourceware.labs.idp.service.AuthService;
import com.sourceware.labs.idp.service.AwsEmailService;
import com.sourceware.labs.idp.util.RestError;
import com.sourceware.labs.idp.util.SignupData;

import io.swagger.v3.oas.annotations.Operation;
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
@RequestMapping(path = "/user")
public class UserController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);
	private static final String BASE_PATH = "/user";
	private static final String SIGNUP_PATH = "/signup";

	@Autowired
	private final UserRepo userRepo;
	
	@Autowired
	private final RoleRepo roleRepo;
	
	@Autowired
	private final AccountVerificationRepo accountVerificationRepo;
	
	@Autowired
	private final AwsEmailService awsEmailService;
	
	@Autowired
	private final AuthService authService;
	
	UserController(UserRepo userRepo, RoleRepo roleRepo, AccountVerificationRepo accountVerificationRepo, AwsEmailService awsEmailService, AuthService authService) {
		this.userRepo = userRepo;
		this.roleRepo = roleRepo;
		this.accountVerificationRepo = accountVerificationRepo;
		this.awsEmailService = awsEmailService;
		this.authService = authService;
	}
	
	@ApiResponses({
		@ApiResponse(responseCode = "201", description = "User successfully signed up", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))}),
		@ApiResponse(responseCode = "400", description = "User failed to be signed up", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = RestError.class))})
	})
	@Operation(summary = "Signup", description = "Signup a new user with the IDP service")
	@Tag(name = "post", description = "POST methods for User APIs")
	@PostMapping(SIGNUP_PATH)
	String signup(@RequestBody SignupData signupData, HttpServletResponse response) throws IOException {
		Optional<RestError> error = signupData.isDataValid(getRoutePath(SIGNUP_PATH), RequestMethod.POST);
		if (error.isPresent()) {
			response.sendError(HttpStatus.SC_BAD_REQUEST, error.get().toString());
			return "";
		} else {
			String verificationToken = "testToken";
			User user = userRepo.save(createNewUser(signupData, verificationToken));
			awsEmailService.sendMessage(awsEmailService.createSimpleMailMessage(user.getEmail(), "Sourceware Labs IDP User Verification", awsEmailService.createVerificatioEmailBody(user.getId(), verificationToken)));
			response.setStatus(HttpStatus.SC_CREATED);
			return "User Created Successfully";
		}
	}
	
	private User createNewUser(SignupData signupData, String verificationToken) {
		Timestamp ts = new Timestamp(new Date().getTime());
		User user = new User(
				null,
				signupData.getEmail(),
				signupData.getPassword(),
				false,
				signupData.getFirstName(),
				signupData.getLastName(),
				signupData.getDob(),
				ts,
				ts
				);
		SecurityQuestion sq = new SecurityQuestion(
				null,
				signupData.getSq1(),
				signupData.getSq2(),
				signupData.getSa1(),
				signupData.getSa2(),
				ts,
				ts,
				null);
		user.setSecurityQuestion(sq);
		user.setRoles(Set.of(roleRepo.findRoleByApplicationAndRole(Application.RealQuick, RoleName.User).get(0)));
		user.setAccountVerification(new AccountVerification(null, verificationToken, user));
		return user;
	}
	
	private String getRoutePath(String path) {
		return BASE_PATH + "/" + path;
	}
	
}
