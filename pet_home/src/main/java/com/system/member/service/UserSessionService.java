package com.system.member.service;

import com.system.member.entity.UserBean;
import com.system.member.entity.UserSessionBean;
import com.system.member.repository.UserSessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserSessionService {
    @Autowired
    private UserSessionRepository userSessionRepository;

    @Transactional
    public void revokeSession(Long userId, String reason){
        List<UserSessionBean> session = userSessionRepository.findByUser_IdAndIsActiveTrue(userId);

        if(session.isEmpty()){
            return;
        }
        LocalDateTime now = LocalDateTime.now();
        for (UserSessionBean user : session) {
            user.setRevokedAt(now);
            user.setRevokedReason(reason);
            user.setIsActive(false);
            user.setUpdatedDate(now);
        };
        userSessionRepository.saveAll(session);
    }

    @Transactional
    public void revokeSessionsByRoleId(Long roleId, String reason) {
        userSessionRepository.revokeActiveSessionsByRoleId(
                roleId,
                reason,
                LocalDateTime.now()
        );
    }

    @Transactional
    public void createSession(UserBean user, String refreshTokenHash,
                           String ipAddress, String userAgent, LocalDateTime expiresAt){
        UserSessionBean session = new UserSessionBean();
        session.setUser(user);
        session.setRefreshTokenHash(refreshTokenHash);
        session.setIpAddress(ipAddress);
        session.setUserAgent(userAgent);
        session.setIsActive(true);
        session.setLoginAt(LocalDateTime.now());
        session.setLastUsedAt(LocalDateTime.now());
        session.setExpiresAt(expiresAt);

        userSessionRepository.save(session);
    }
}
