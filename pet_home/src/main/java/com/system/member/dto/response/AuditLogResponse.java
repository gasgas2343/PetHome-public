package com.system.member.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class AuditLogResponse {
    private Long id;
    private Long actorId;
    private String actorEmail;
    private String action;
    private String moduleCode;
    private String targetType;
    private Long targetId;
    private String metadata;
    private String ipAddress;
    private String userAgent;
    private LocalDateTime createdAt;
}
