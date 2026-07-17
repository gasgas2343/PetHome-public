package com.system.petpost.dto.admin;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class AdminNotificationDTO {

    private Long notificationId;

    private Long userId;
    private String userName;
    private String userEmail;

    private String type;
    private String message;
    private Boolean isRead;

    private String referenceType;
    private Long referenceId;

    private LocalDateTime createdAt;
}