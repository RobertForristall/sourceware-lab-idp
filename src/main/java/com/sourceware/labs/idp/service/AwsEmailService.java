package com.sourceware.labs.idp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

/**
 * Spring service class for sending mail using AWS Simple Email Service (SES)
 *
 * @author Robert Forristall (robert.s.forristall@gmail.com)
 */
@Service
public class AwsEmailService {

	// Inject the mail sender configured to use AWS
	@Autowired
	private MailSender mailSender;
	
	// Get the verified from email address to use for all sent messages
	@Value("${cloud.aws.email.from}")
	public String fromAddress;
	
	/**
	 * Send a simple email message using AWS SES
	 * @param simpleMailMessage - The message information to be sent
	 */
	public void sendMessage(SimpleMailMessage simpleMailMessage) {
		this.mailSender.send(simpleMailMessage);
	}
	
	/**
	 * Helper function for creating a simple email message
	 * @param toAddress - The email address to send the message to
	 * @param subject - The subject of the email
	 * @param body - The body/text of the email
	 * @return {@link SimpleMailMessage} configured using the provided parameters
	 */
	public SimpleMailMessage createSimpleMailMessage(String toAddress, String subject, String body) {
		SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
		simpleMailMessage.setFrom(fromAddress);
		simpleMailMessage.setTo(toAddress);
		simpleMailMessage.setSubject(subject);
		simpleMailMessage.setText(body);
		return simpleMailMessage;
	}
	
}
