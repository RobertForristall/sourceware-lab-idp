package com.sourceware.labs.idp.route;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.net.URI;
import java.net.URISyntaxException;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.sourceware.labs.idp.BaseIdpApplicationTests;
import com.sourceware.labs.idp.controller.UserController;
import com.sourceware.labs.idp.service.AwsEmailService;
import com.sourceware.labs.idp.test.data.TestData;
import com.sourceware.labs.idp.util.SignupData;

@TestInstance(Lifecycle.PER_CLASS)
public class SignupTests extends BaseIdpApplicationTests{

	private URI testingRoute;
	
	@Mock
	AwsEmailService awsEmailService;
	
	@InjectMocks
	UserController userController;
	
	private MockMvc mockMvc;
	
	@BeforeAll
	public void setup() throws URISyntaxException {
		testingRoute = new URI(baseUrl + "user/signup");
        MockitoAnnotations.openMocks(userController);

        this.mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
	}
	
	@Test
	@Disabled
	public void createValidUser() throws Exception {
		SignupData signupData = TestData.getTestSignupData();
//		HttpEntity<SignupData> request = new HttpEntity<>(signupData, new HttpHeaders());
//		ResponseEntity<String> result = this.restTemplate.postForEntity(testingRoute, request, String.class);
//		Assertions.assertEquals(HttpStatus.CREATED, result.getStatusCode());
//		Assertions.assertEquals("User Created Successfully", result.getBody());
		mockMvc.perform(
				post(testingRoute).contentType(MediaType.APPLICATION_JSON).content(signupData.toString())
		).andExpect(status().isCreated());
	}
	
//	@Test
//	public void catchNullRequiredValue() {
//		
//	}
//	
//	@Test
//	public void catchDuplicateUniqueValue() {
//		
//	}
//	
//	@Test
//	public void catchEmptyRequiredValue() {
//		
//	}
//	
//	@Test
//	public void catchInvalidEmail() {
//		
//	}
//	
//	@Test
//	public void catchInvalidDob() {
//		
//	}
//	
//	@Test
//	public void catchInvalidRecoveryEmail() {
//		
//	}
//	
//	@Test
//	public void catchInvalidRecoveryPhone() {
//		
//	}
	
}
