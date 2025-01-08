package com.sourceware.labs.idp.controller;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sourceware.labs.idp.entity.AccountVerification;
import com.sourceware.labs.idp.entity.Role.Application;
import com.sourceware.labs.idp.entity.Role.RoleName;
import com.sourceware.labs.idp.entity.SecurityQuestion;
import com.sourceware.labs.idp.entity.User;
import com.sourceware.labs.idp.repo.AccountVerificationRepo;
import com.sourceware.labs.idp.repo.RoleRepo;
import com.sourceware.labs.idp.repo.UserRepo;
import com.sourceware.labs.idp.service.AuthService;
import com.sourceware.labs.idp.service.AwsEmailService;
import com.sourceware.labs.idp.util.RestError;
import com.sourceware.labs.idp.util.RestError.RestErrorBuilder;
import com.sourceware.labs.idp.util.SignupData;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolationException;

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
	private static final String VERIFY_PATH = "/verify/{userId}/{verificationToken}";

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
		@ApiResponse(responseCode = "400", description = "User failed to be signed up", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = RestError.class))}),
		@ApiResponse(responseCode = "409", description = "User provided an email that is already associated with an account", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = RestError.class))})
	})
	@Operation(summary = "Signup", description = "Signup a new user with the IDP service")
	@Tag(name = "post", description = "POST methods for User APIs")
	@PostMapping(SIGNUP_PATH)
	String signup(@RequestBody SignupData signupData, HttpServletResponse response) throws IOException {
		Optional<RestError> error = signupData.isDataValid(getRoutePath(SIGNUP_PATH), RequestMethod.POST);
		if (error.isPresent()) {
			response.sendError(HttpStatus.BAD_REQUEST.value(), error.get().toString());
		} else {
			try {
				User user = userRepo.save(createNewUser(signupData));
				awsEmailService.sendMessage(awsEmailService.createSimpleMailMessage(user.getEmail(), "Sourceware Labs IDP User Verification", awsEmailService.createVerificatioEmailBody(user.getId(), signupData.getVerificationToken())));
				response.setStatus(HttpStatus.CREATED.value());
				return "User Created Successfully";
			} catch (Exception ex) {
				if (ex.getClass().equals(DataIntegrityViolationException.class)) {
					// TODO find a better way to catch this SQL error than parsing the message
					if (ex.getLocalizedMessage().contains("Detail: Key (email)=("+signupData.getEmail()+") already exists")) {
						response.sendError(HttpStatus.CONFLICT.value(), new RestErrorBuilder().setRoute(getRoutePath(SIGNUP_PATH)).setMethod(RequestMethod.POST).setErrorCode(5).setMsg("Error: A user with the provided email already exists").build().toString());
					}
					
				} else if (ex.getClass().equals(TransactionSystemException.class)) {
					TransactionSystemException transactionEx = (TransactionSystemException) ex;
					if (transactionEx.getRootCause().getClass().equals(ConstraintViolationException.class)) {
						ConstraintViolationException constraintEx = (ConstraintViolationException) transactionEx.getRootCause();
						response.sendError(HttpStatus.BAD_REQUEST.value(), new RestErrorBuilder().setRoute(getRoutePath(SIGNUP_PATH)).setMethod(RequestMethod.POST).setErrorCode(6).setMsg("Error: " + constraintEx.getConstraintViolations().stream().findFirst().get().getMessageTemplate()).build().toString());
					}
				} else {
					throw ex;
				}
			}
		}
		return null;
	}
	
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "User is successfully verified", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))}),
		@ApiResponse(responseCode = "400", description = "User failed to verify", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = RestError.class))})
	})
	@Operation(summary = "Verify User Account", description = "Verify a new user's account")
	@Tag(name = "get", description = "GET methods for User APIs")
	@GetMapping(VERIFY_PATH)
	String verify(@Parameter(description = "ID of the user to verify", required = true) @PathVariable Long userId,
			@Parameter(description = "Verification token for the user to be verified", required = true) @PathVariable String verificationToken,
			HttpServletResponse response) {
		if (userId == null || verificationToken == null) {
			// TODO handle null userId or null verificationToken
			return "";
		} else {
			List<AccountVerification> verifications = accountVerificationRepo.findAccountVerificationByUserIdAndVerificationToken(userId, verificationToken);
			if (verifications.size() == 1) {
				User user = userRepo.getReferenceById(userId);
				user.setVerified(true);
				user = userRepo.save(user);
				response.setStatus(HttpStatus.OK.value());
				return "User successfully verified";
			} else {
				// TODO handle non-1 verifications size
				return "";
			}
		}
	}
	
	private User createNewUser(SignupData signupData) {
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
		user.setAccountVerification(new AccountVerification(null, signupData.getVerificationToken(), user));
		return user;
	}
	
	private String getRoutePath(String path) {
		return BASE_PATH + path;
	}
	
}
