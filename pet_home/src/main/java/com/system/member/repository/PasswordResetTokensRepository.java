package com.system.member.repository;

import com.system.member.entity.PasswordResetTokenBean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PasswordResetTokensRepository extends JpaRepository<PasswordResetTokenBean, Long> {
    Optional<PasswordResetTokenBean> findByTokenHash(String token);
}
