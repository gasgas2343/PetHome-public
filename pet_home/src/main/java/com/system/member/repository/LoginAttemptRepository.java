package com.system.member.repository;

import com.system.member.entity.LoginAttemptBean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoginAttemptRepository extends JpaRepository<LoginAttemptBean, Long> {
    List<LoginAttemptBean> findAllByOrderByAttemptedAtDesc();
}
