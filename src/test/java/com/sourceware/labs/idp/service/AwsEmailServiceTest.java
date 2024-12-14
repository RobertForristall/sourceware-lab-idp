package com.sourceware.labs.idp.service;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.mail.SimpleMailMessage;

@TestInstance(Lifecycle.PER_CLASS)
public class AwsEmailServiceTest{
	
	private final String fromAddress = "from@test.com";
	
	private AwsEmailService awsEmailService;

	@BeforeAll
	public void setup() {
		awsEmailService = new AwsEmailService();
		awsEmailService.fromAddress = fromAddress;
	}
	
	@Test
	public void testCreateSimpleEmailMessage() {
		String toEmail = "to@test.com";
		String subject = "Test Subject";
		String body = "Testing the email service";
		SimpleMailMessage simpleMailMessage = awsEmailService.createSimpleMailMessage(toEmail, subject, body);
		Assertions.assertEquals(fromAddress, simpleMailMessage.getFrom());
		Assertions.assertEquals(toEmail, List.of(simpleMailMessage.getTo()).get(0));
		Assertions.assertEquals(subject, simpleMailMessage.getSubject());
		Assertions.assertEquals(body, simpleMailMessage.getText());
	}
	
}
