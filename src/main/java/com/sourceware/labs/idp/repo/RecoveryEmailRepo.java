package com.sourceware.labs.idp.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sourceware.labs.idp.entity.RecoveryEmail;

public interface RecoveryEmailRepo extends JpaRepository<RecoveryEmail, Long>{

}
