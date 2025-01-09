package com.sourceware.labs.idp.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sourceware.labs.idp.entity.AdditionalPermission;

public interface AdditionalPermissionRepo extends JpaRepository<AdditionalPermission, Long> {

}
