package com.sourceware.labs.idp.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sourceware.labs.idp.entity.User;

public interface UserRepository extends JpaRepository<User, Long>{

}
