package com.sourceware.labs.idp.test.data;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

import com.sourceware.labs.idp.entity.Role;
import com.sourceware.labs.idp.entity.Role.Application;
import com.sourceware.labs.idp.entity.Role.RoleName;
import com.sourceware.labs.idp.entity.SecurityQuestion;
import com.sourceware.labs.idp.entity.User;
import com.sourceware.labs.idp.util.SignupData;

public class TestData {
	
	private static Timestamp ts = new Timestamp(new Date().getTime());
	
	public static SecurityQuestion getTestSecurityQuestion() {
		SecurityQuestion sq = new SecurityQuestion();
		sq.setQuestion1("TestQ1");
		sq.setQuestion2("TestQ2");
		sq.setAnswer1("TestA1");
		sq.setAnswer2("TestA2");
		sq.setCreated(ts);
		sq.setModified(ts);
		return sq;
	}
	
	public static Role getTestRole() {
		Role role = new Role();
		role.setApplication(Application.RealQuick);
		role.setRole(RoleName.User);
		role.setRoleDescription("Testing");
		return role;
	}
	
	public static SignupData getTestSignupData() {
		SecurityQuestion sq = getTestSecurityQuestion();
		SignupData user = new SignupData();
		user.setEmail("test@test.com");
		user.setPassword("testPass");
		user.setFirstName("Test");
		user.setLastName("User");
		try {
			user.setDob(new SimpleDateFormat("yyyy-MM-dd").parse("2001-01-01"));
		} catch (ParseException ex) {
			// Since the data is hard coded this should never happen
			ex.printStackTrace();
		}
		user.setSq1(sq.getQuestion1());
		user.setSq2(sq.getQuestion2());
		user.setSa1(sq.getAnswer1());
		user.setSa2(sq.getAnswer2());
		return user;
	}
	
}
