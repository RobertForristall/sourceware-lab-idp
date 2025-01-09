package com.sourceware.labs.idp.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sourceware.labs.idp.entity.RecoveryVerification;

public interface RecoveryVerificationRepo extends JpaRepository<RecoveryVerification, Long> {

}
