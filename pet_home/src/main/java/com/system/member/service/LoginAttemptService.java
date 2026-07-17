package com.system.member.service;

import com.system.member.entity.LoginAttemptBean;
import com.system.member.entity.UserBean;
import com.system.member.repository.LoginAttemptRepository;
import com.system.member.repository.UsersRepository;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class LoginAttemptService {
    @Autowired
    private UsersRepository usersRepository;
    @Autowired
    private LoginAttemptRepository loginAttemptRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void createFailureRecord(String email, HttpServletRequest request, String reason){
        UserBean user = usersRepository.findByEmail(email)
                .orElse(null);

        String ipAddress = request.getRemoteAddr();
        String userAgent = request.getHeader("User-Agent");
        LocalDateTime now = LocalDateTime.now();

        LoginAttemptBean loginAttempt = new LoginAttemptBean();
        loginAttempt.setUser(user);
        loginAttempt.setEmail(email);
        loginAttempt.setIpAddress(ipAddress);
        loginAttempt.setUserAgent(userAgent);
        loginAttempt.setSuccess(false);
        loginAttempt.setFailureReason(reason);
        loginAttempt.setAttemptedAt(now);
        loginAttemptRepository.save(loginAttempt);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void createSuccessRecord(UserBean user, HttpServletRequest request){

        String ipAddress = request.getRemoteAddr();
        String userAgent = request.getHeader("User-Agent");
        LocalDateTime now = LocalDateTime.now();

        LoginAttemptBean loginAttempt = new LoginAttemptBean();
        loginAttempt.setUser(user);
        loginAttempt.setEmail(user.getEmail());
        loginAttempt.setIpAddress(ipAddress);
        loginAttempt.setUserAgent(userAgent);
        loginAttempt.setSuccess(true);
        loginAttempt.setAttemptedAt(now);
        loginAttemptRepository.save(loginAttempt);
    }
}
