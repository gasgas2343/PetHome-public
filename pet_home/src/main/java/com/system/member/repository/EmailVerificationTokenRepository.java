package com.system.member.repository;

import com.system.member.entity.EmailVerificationTokenBean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmailVerificationTokenRepository extends JpaRepository<EmailVerificationTokenBean, Long> {

    Optional<EmailVerificationTokenBean> findByUser_IdAndVerificationType(Long userId, String verificationType);

    Optional<EmailVerificationTokenBean> findByTokenHashAndVerificationType(String tokenHash, String verificationType);
}
