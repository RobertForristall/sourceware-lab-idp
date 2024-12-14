package com.sourceware.labs.idp.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sourceware.labs.idp.entity.SecurityQuestion;

public interface SecurityQuestionRepo  extends JpaRepository<SecurityQuestion, Long>{

}
