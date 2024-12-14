package com.sourceware.labs.idp.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sourceware.labs.idp.entity.Role;

public interface RoleRepo extends JpaRepository<Role, Long>{

}
