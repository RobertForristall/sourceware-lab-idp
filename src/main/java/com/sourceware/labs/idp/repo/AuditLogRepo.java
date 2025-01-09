package com.sourceware.labs.idp.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sourceware.labs.idp.entity.AuditLog;

public interface AuditLogRepo extends JpaRepository<AuditLog, Long> {

}
