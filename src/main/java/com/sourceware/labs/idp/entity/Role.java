package com.sourceware.labs.idp.entity;

import java.util.Objects;
import java.util.Set;

import com.google.gson.Gson;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "Roles")
public class Role {
	
	public enum Application {
		RealQuick
	}
	
	public enum RoleName {
		User, Admin
	}

	private @Id @GeneratedValue @NotNull Long id;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "application")
	private Application application;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "role")
	private RoleName role;
	
	private String roleDescription;
	
	@ManyToMany
	private Set<User> users;

	public Role() {
		super();
	}

	public Role(@NotNull Long id, Application application, RoleName role, String roleDescription, Set<User> users) {
		super();
		this.id = id;
		this.application = application;
		this.role = role;
		this.roleDescription = roleDescription;
		this.users = users;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Application getApplication() {
		return application;
	}

	public void setApplication(Application application) {
		this.application = application;
	}

	public RoleName getRole() {
		return role;
	}

	public void setRole(RoleName role) {
		this.role = role;
	}

	public String getRoleDescription() {
		return roleDescription;
	}

	public void setRoleDescription(String roleDescription) {
		this.roleDescription = roleDescription;
	}

	public Set<User> getUsers() {
		return users;
	}

	public void setUsers(Set<User> users) {
		this.users = users;
	}

	@Override
	public int hashCode() {
		return Objects.hash(application, id, role, roleDescription, users);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Role other = (Role) obj;
		return application == other.application && Objects.equals(id, other.id) && role == other.role
				&& Objects.equals(roleDescription, other.roleDescription) && Objects.equals(users, other.users);
	}

	@Override
	public String toString() {
		return new Gson().toJson(this);
	}
	
	
}
