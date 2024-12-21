package com.sourceware.labs.idp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.sourceware.labs.idp.service.AwsEmailService;

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
		    "server.port=8090",
		    "cloud.aws.email.from=from@test.com",
		    "cloud.aws.region.static=us-east-1",
		    "cloud.aws.region.auto=false",
		    "cloud.aws.stack.auto=false",
		    "cloud.aws.credentials.access-key=testAccessKey",
		    "cloud.aws.credentials.secret-key=testSecretKey",
		    "keystore.store.password=testPass",
		    "keystore.key.token.password=testKeyPass",
		    "spring.datasource.hikari.schema=public"
		  })
public abstract class BaseIdpApplicationTests {

	/**
	 * Test container created in docker that contains a MySQL database whose connection properties
	 * are injected into the Spring server for testing database interaction
	 */
	@Container
	@ServiceConnection
	protected static PostgreSQLContainer<?> postgres = new PostgreSQLContainer("postgres:17");

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
	
	@MockitoBean
	protected AwsEmailService awsEmailService;

	/**
	 * Base URL of the testing Spring server
	 */
	protected final String baseUrl = "http://localhost:" + port + "/";

}
