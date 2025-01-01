package com.sourceware.labs.idp.route;

import java.net.URI;
import java.net.URISyntaxException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMethod;

import com.google.gson.Gson;
import com.sourceware.labs.idp.BaseIdpApplicationTests;
import com.sourceware.labs.idp.test.data.TestData;
import com.sourceware.labs.idp.util.HttpErrorResponse;
import com.sourceware.labs.idp.util.RestError;
import com.sourceware.labs.idp.util.RestError.RestErrorBuilder;
import com.sourceware.labs.idp.util.SignupData;

@TestInstance(Lifecycle.PER_CLASS)
@TestMethodOrder(OrderAnnotation.class)
public class SignupTests extends BaseIdpApplicationTests{

	private URI testingRoute;
	
	@BeforeAll
	public void setup() throws URISyntaxException {
		testingRoute = new URI(baseUrl + "user/signup");
	}
	
	@Test
	@Order(1)
	public void createValidUser() throws Exception {
		SignupData signupData = TestData.getTestSignupData();
		HttpEntity<SignupData> request = new HttpEntity<>(signupData, new HttpHeaders());
		ResponseEntity<String> result = this.restTemplate.postForEntity(testingRoute, request, String.class);
		Assertions.assertEquals(HttpStatusCode.valueOf(201), result.getStatusCode());
		Assertions.assertEquals("User Created Successfully", result.getBody());
	}
	
	@Test
	public void catchEmailIsNull() {
		SignupData signupData = TestData.getTestSignupData();
		signupData.setEmail(null);
		ResponseEntity<String> result = issueRequest(signupData);
		assertRestErrorsEqual(result, 400, 1, "Error: User's email is not valid");
	}
	
	@Test
	public void catchEmailIsEmpty() {
		SignupData signupData = TestData.getTestSignupData();
		signupData.setEmail("");
		ResponseEntity<String> result = issueRequest(signupData);
		assertRestErrorsEqual(result, 400, 1, "Error: User's email is not valid");
	}
	
	@Test
	public void catchEmailIsNotValid() {
		SignupData signupData = TestData.getTestSignupData();
		signupData.setEmail("test@test");
		ResponseEntity<String> result = issueRequest(signupData);
		assertRestErrorsEqual(result, 400, 1, "Error: User's email is not valid");
	}
	
	@Test
	public void catchEmailIsNotUnique() {
		SignupData signupData = TestData.getTestSignupData();
		ResponseEntity<String> result = issueRequest(signupData);
		assertRestErrorsEqual(result, 409, 5, "Error: A user with the provided email already exists");
	}
	
	private ResponseEntity<String> issueRequest(SignupData data) {
		HttpEntity<SignupData> request = new HttpEntity<>(data, new HttpHeaders());
		return this.restTemplate.postForEntity(testingRoute, request, String.class);
	}
	
	private void assertRestErrorsEqual(ResponseEntity<String> result, int responseCode, int errorCode, String msg) {
		Assertions.assertEquals(HttpStatusCode.valueOf(responseCode), result.getStatusCode());
		RestError expectedError = getRestError(errorCode, msg);
		RestError actualError = new Gson().fromJson(result.getBody(), HttpErrorResponse.class).getMessageAsRestError();
		Assertions.assertEquals(expectedError, actualError);
	}
	
	private RestError getRestError(int code, String msg) {
		return new RestErrorBuilder().setRoute("/user/signup").setMethod(RequestMethod.POST).setErrorCode(code).setMsg(msg).build();
	}
	
}
