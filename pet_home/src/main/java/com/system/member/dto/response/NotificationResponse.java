package com.system.member.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NotificationResponse {
    private Long id;
    private String moduleCode;
    private String notificationType;
    private String title;
    private String content;
    private String targetType;
    private Long targetId;
    @JsonProperty("isRead")
    private boolean isRead;
    private LocalDateTime readAt;
    private LocalDateTime createdAt;
}
