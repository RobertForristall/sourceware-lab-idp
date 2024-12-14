package com.sourceware.labs.idp.route;

import java.net.URI;
import java.sql.Timestamp;
import java.util.Date;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.sourceware.labs.idp.BaseIdpApplicationTests;
import com.sourceware.labs.idp.entity.User;

/**
 * Testing class for the Users route of the IDP server
 *
 * @author Robert Forristall (robert.s.forristall@gmail.com)
 */
public class UserTests extends BaseIdpApplicationTests{

	/**
	 * Testing the POST /user route for creating a new user
	 * @throws Exception
	 * TODO remove this test when the user route is reconfigured to use a proper signup method
	 */
	@Test
	void postUser() throws Exception {
		URI uri = new URI(baseUrl + "user");
		Date now = new Date();
		Timestamp timeStamp = new Timestamp(now.getTime());
		User user = new User(null, "test@test.com", "pass", false, "testName", "testLastName", now, timeStamp, timeStamp);
		HttpEntity<User> request = new HttpEntity<>(user, new HttpHeaders());
		ResponseEntity<User> result = this.restTemplate.postForEntity(uri, request, User.class);
		Assertions.assertEquals(HttpStatus.CREATED, result.getStatusCode());
		Assertions.assertEquals(result.getBody().getEmail(), user.getEmail());
	}
	
}
