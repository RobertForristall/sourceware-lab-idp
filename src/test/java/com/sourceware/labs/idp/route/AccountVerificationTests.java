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

import com.sourceware.labs.idp.BaseIdpApplicationTests;
import com.sourceware.labs.idp.test.data.TestData;
import com.sourceware.labs.idp.util.SignupData;

@TestInstance(Lifecycle.PER_CLASS)
@TestMethodOrder(OrderAnnotation.class)
public class AccountVerificationTests extends BaseIdpApplicationTests{
	
	private String testingPath;
	private RequestMethod testingMethod;
	private URI fullTestingRoute;
	
	private String verificationToken;
	
	private Long userId;
	
	@BeforeAll
	public void setup() throws URISyntaxException {
		testingPath = "/user/verify";
		testingMethod = RequestMethod.GET;
		fullTestingRoute = new URI(baseUrl + testingPath);
		SignupData signupData = TestData.getTestSignupData();
		HttpEntity<SignupData> request = new HttpEntity<>(signupData, new HttpHeaders());
		ResponseEntity<String> result = this.restTemplate.postForEntity(baseUrl + "/user/signup", request, String.class);
		Assertions.assertEquals(HttpStatusCode.valueOf(201), result.getStatusCode());
		Assertions.assertEquals("User Created Successfully", result.getBody());
		verificationToken = signupData.getVerificationToken();
		userId = Long.valueOf(1);
	}
	
	@Test
	@Order(1)
	public void successfullyVerifyUser() {
		ResponseEntity<String> result = this.restTemplate.getForEntity(fullTestingRoute + getPathVariables(String.valueOf(userId.intValue()), verificationToken), String.class);
		Assertions.assertEquals(HttpStatusCode.valueOf(200), result.getStatusCode());
		Assertions.assertEquals("User successfully verified", result.getBody());
	}
	
	@Test
	public void catchUserIdNull() {
		ResponseEntity<String> result = this.restTemplate.getForEntity(fullTestingRoute + getPathVariables(null, verificationToken), String.class);
		Assertions.assertEquals(HttpStatusCode.valueOf(404), result.getStatusCode());
	}
	
	@Test
	public void catchVerificationTokenNull() {
		ResponseEntity<String> result = this.restTemplate.getForEntity(fullTestingRoute + getPathVariables(String.valueOf(userId.intValue()), null), String.class);
		Assertions.assertEquals(HttpStatusCode.valueOf(404), result.getStatusCode());
	}
	
	@Test
	public void catchOnlyUserIdProvided() {
		ResponseEntity<String> result = this.restTemplate.getForEntity(fullTestingRoute + "/" + String.valueOf(userId.intValue()), String.class);
		Assertions.assertEquals(HttpStatusCode.valueOf(404), result.getStatusCode());
	}
	
	@Test
	public void catchOnlyVerificationTokenProvided() {
		ResponseEntity<String> result = this.restTemplate.getForEntity(fullTestingRoute + "/" + verificationToken, String.class);
		Assertions.assertEquals(HttpStatusCode.valueOf(404), result.getStatusCode());
	}
	
	@Test
	public void catchInvalidUserIdType() {
		ResponseEntity<String> result = this.restTemplate.getForEntity(fullTestingRoute + "/" + String.valueOf("test") + "/" + verificationToken, String.class);
		assertRestErrorsEqual(result, testingPath, testingMethod, 400, 1, "Error: user ID is not of type Integer");
	}
	
	@Test
	public void catchNoAccountVerificationEntryFound() {
		ResponseEntity<String> result = this.restTemplate.getForEntity(fullTestingRoute + getPathVariables(String.valueOf(999), verificationToken), String.class);
		assertRestErrorsEqual(result, testingPath, testingMethod, 400, 3, "Error: no entry in account validation table found");
	}
	
	private String getPathVariables(String userId, String verificationToken) {
		return "/%s/%s".formatted(userId != null ? userId : "",verificationToken != null ? verificationToken : "");
	}
}
