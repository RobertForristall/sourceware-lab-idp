package com.sourceware.labs.idp.entity;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Objects;

import com.google.gson.Gson;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Spring Boot Entity class for a User in the IDP server.
 * 
 * @author Robert Forristall
 */
@Entity
@Table(name = "Users")
public class User {

	// Internal database ID for the user
	private @Id @GeneratedValue @NotNull Long id;

	// Email that the user will use to signup/login
	@Size(min=1, max=50)
	private String email;

	// Password for the user
	private String password;

	// If the user has verified their email address
	private boolean verified;

	// First name of the user
	private String firstName;

	// Last name of the user
	private String lastName;

	// Date of birth for the user
	private Date dob;

	// Timestamp of when the user was created
	private Timestamp created;

	// Timestamp of when the user was last modified
	private Timestamp modified;
	
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "securityQuestionId", referencedColumnName = "id")
	private SecurityQuestion securityQuestion;
	
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "recoveryEmail", referencedColumnName = "id")
	private RecoveryEmail recoveryEmail;

	public User() {
		super();
	}

	public User(Long id, String email, String password, boolean verified, String firstName, String lastName, Date dob,
			Timestamp created, Timestamp modified) {
		super();
		this.id = id;
		this.email = email;
		this.password = password;
		this.verified = verified;
		this.firstName = firstName;
		this.lastName = lastName;
		this.dob = dob;
		this.created = created;
		this.modified = modified;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isVerified() {
		return verified;
	}

	public void setVerified(boolean verified) {
		this.verified = verified;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public Date getDob() {
		return dob;
	}

	public void setDob(Date dob) {
		this.dob = dob;
	}

	public Timestamp getCreated() {
		return created;
	}

	public void setCreated(Timestamp created) {
		this.created = created;
	}

	public Timestamp getModified() {
		return modified;
	}

	public void setModified(Timestamp modified) {
		this.modified = modified;
	}

	@Override
	public int hashCode() {
		return Objects.hash(created, dob, email, firstName, id, lastName, modified, password, verified);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		return Objects.equals(created, other.created) && Objects.equals(dob, other.dob)
				&& Objects.equals(email, other.email) && Objects.equals(firstName, other.firstName)
				&& Objects.equals(id, other.id) && Objects.equals(lastName, other.lastName)
				&& Objects.equals(modified, other.modified) && Objects.equals(password, other.password)
				&& verified == other.verified;
	}

	@Override
	public String toString() {
		return new Gson().toJson(this);
	}

}
