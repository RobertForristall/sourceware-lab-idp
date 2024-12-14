package com.sourceware.labs.idp.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sourceware.labs.idp.entity.AccountVerification;

public interface AccountVerificationRepo extends JpaRepository<AccountVerification, Long>{

}