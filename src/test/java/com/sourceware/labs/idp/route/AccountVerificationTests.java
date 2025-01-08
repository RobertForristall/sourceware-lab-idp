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

import com.sourceware.labs.idp.BaseIdpApplicationTests;
import com.sourceware.labs.idp.test.data.TestData;
import com.sourceware.labs.idp.util.SignupData;

@TestInstance(Lifecycle.PER_CLASS)
@TestMethodOrder(OrderAnnotation.class)
public class AccountVerificationTests extends BaseIdpApplicationTests{
	
	private URI testingRoute;
	
	private String verificationToken;
	
	private Long userId;
	
	@BeforeAll
	public void setup() throws URISyntaxException {
		testingRoute = new URI(baseUrl + "user/verify");
		SignupData signupData = TestData.getTestSignupData();
		HttpEntity<SignupData> request = new HttpEntity<>(signupData, new HttpHeaders());
		ResponseEntity<String> result = this.restTemplate.postForEntity(baseUrl + "user/signup", request, String.class);
		Assertions.assertEquals(HttpStatusCode.valueOf(201), result.getStatusCode());
		Assertions.assertEquals("User Created Successfully", result.getBody());
		verificationToken = signupData.getVerificationToken();
		userId = Long.valueOf(1);
	}
	
	@Test
	@Order(1)
	public void successfullyVerifyUser() {
		ResponseEntity<String> result = this.restTemplate.getForEntity(testingRoute + getPathVariables(), String.class);
		Assertions.assertEquals(HttpStatusCode.valueOf(200), result.getStatusCode());
		Assertions.assertEquals("User successfully verified", result.getBody());
	}
	
	private String getPathVariables() {
		return "/%d/%s".formatted(userId.intValue(),verificationToken);
	}
}
