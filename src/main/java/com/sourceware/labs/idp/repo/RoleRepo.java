package com.sourceware.labs.idp.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sourceware.labs.idp.entity.Role;
import com.sourceware.labs.idp.entity.Role.Application;
import com.sourceware.labs.idp.entity.Role.RoleName;

public interface RoleRepo extends JpaRepository<Role, Long>{
	List<Role> findRoleByApplicationAndRole(Application application, RoleName role);
}
