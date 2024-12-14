package com.sourceware.labs.idp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * Base testing class for all tests that need to use the Spring Boot server during the test
 * <br/>
 * Creates an underlying test container to act as a mock database during the process of the tests
 *
 * @author Robert Forristall (robert.s.forristall@gmail.com)
 */
@Testcontainers
@SpringBootTest(
		  webEnvironment = WebEnvironment.DEFINED_PORT,
		  properties = {
		    "server.port=8090"
		  })
public abstract class BaseIdpApplicationTests {

	/**
	 * Test container created in docker that contains a MySQL database whose connection properties
	 * are injected into the Spring server for testing database interaction
	 */
	@Container
	@ServiceConnection
	protected static MySQLContainer<?> mysql = new MySQLContainer("mysql:8.0");

	/**
	 * The port that the server is running on
	 * TODO remove this once SpringBootTest properties is fixed so it works on its own
	 */
	int port = 8090;

	/**
	 * Injected testing rest access point for sending requests to the server
	 */
	@Autowired
	protected TestRestTemplate restTemplate;

	/**
	 * Base URL of the testing Spring server
	 */
	protected final String baseUrl = "http://localhost:" + port + "/";

}
