package com.sourceware.labs.idp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

@Service
public class AwsEmailService {

	@Autowired
	private MailSender mailSender;
	
	@Value("${cloud.aws.email.from}")
	public String fromAddress;
	
	public void sendMessage(SimpleMailMessage simpleMailMessage) {
		this.mailSender.send(simpleMailMessage);
	}
	
	public SimpleMailMessage createSimpleMailMessage(String toAddress, String subject, String body) {
		SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
		simpleMailMessage.setFrom(fromAddress);
		simpleMailMessage.setTo(toAddress);
		simpleMailMessage.setSubject(subject);
		simpleMailMessage.setText(body);
		return simpleMailMessage;
	}
	
}
