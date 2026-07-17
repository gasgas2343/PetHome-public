package com.system.petpost.dto.admin;

import lombok.Data;

@Data
public class AdminAppealDTO {
    private Long appealId;

    private Long userId;

    private String userName;

    private String userEmail;

    private String targetType;

    private Long targetId;

    private String appealType;

    private String subject;

    private String reason;

    private Byte status;
}
