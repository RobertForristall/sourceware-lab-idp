package com.sourceware.labs.idp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@SpringBootTest(
		  webEnvironment = WebEnvironment.DEFINED_PORT,
		  properties = {
		    "server.port=8090"
		  })
public abstract class BaseIdpApplicationTests {

	@Container
	@ServiceConnection
	protected static MySQLContainer<?> mysql = new MySQLContainer("mysql:8.0");

	int port = 8090;

	@Autowired
	protected TestRestTemplate restTemplate;

	protected final String baseUrl = "http://localhost:" + port + "/";

}
