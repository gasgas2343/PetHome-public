package com.system.member.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class LoginAttemptResponse {
    private Long id;
    private Long userId;
    private String email;
    private String ipAddress;
    private String userAgent;
    private boolean success;
    private String failureReason;
    private LocalDateTime attemptedAt;
}
