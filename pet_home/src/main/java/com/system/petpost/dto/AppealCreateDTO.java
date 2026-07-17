package com.system.petpost.dto;

import lombok.Data;

@Data
public class AppealCreateDTO {

    private Long reportId;

    private Long userId;

    // 中文註解：申訴目標類型，例如 POST。
    private String targetType;

    // 中文註解：申訴目標 ID，例如文章 ID。
    private Long targetId;

    private String appealType;

    private String subject;

    private String reason;

    private String imageUrl;
}