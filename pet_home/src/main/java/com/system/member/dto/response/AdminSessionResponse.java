package com.system.member.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class AdminSessionResponse {
    private Long id;
    private Long userId;
    private String email;
    private String ipAddress;
    private boolean isActive;
    private LocalDateTime loginAt;
    private LocalDateTime lastUsedAt;
    private LocalDateTime expiresAt;
    private LocalDateTime revokedAt;
    private String revokedReason;
}
