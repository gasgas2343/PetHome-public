package com.system.petpost.dto.admin;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 中文註解：論壇後台檢舉管理 DTO。
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdminReportDTO {

    private Long reportId;

    private Long reporterId;
    private String reporterUserName;
    private String reporterUserEmail;

    private Long reportedUserId;
    private String reportedUserName;
    private String reportedUserEmail;

    private Long postId;
    private Long commentId;

    private String reason;
    private Byte status;
    private LocalDateTime createdAt;
}