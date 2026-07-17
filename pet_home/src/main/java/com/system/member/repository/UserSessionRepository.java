package com.system.member.repository;

import com.system.member.entity.UserSessionBean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserSessionRepository extends JpaRepository<UserSessionBean, Long> {
    List<UserSessionBean> findByUser_IdAndIsActiveTrue(long userId);
    Optional<UserSessionBean> findByRefreshTokenHash(String refreshTokenHash);
    Optional<UserSessionBean> findByRefreshTokenHashAndIsActiveTrue(String refreshTokenHash);
    List<UserSessionBean> findAllByOrderByCreatedDateDesc();

    @Modifying
    @Query("""
    UPDATE UserSessionBean s
    SET s.isActive = false,
        s.revokedAt = :now,
        s.revokedReason = :reason,
        s.updatedDate = :now
    WHERE s.isActive = true
      AND s.user.id IN (
          SELECT ur.user.id
          FROM UserRoleBean ur
          WHERE ur.role.id = :roleId
      )
""")
    int revokeActiveSessionsByRoleId(
            @Param("roleId") Long roleId,
            @Param("reason") String reason,
            @Param("now") LocalDateTime now
    );
}
