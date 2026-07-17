package com.system.petpost.dto;

import lombok.Data;

@Data
public class ReportCreateDTO {

    // 中文註解：檢舉人 ID。
    // 前端不要傳，Controller 會從 JWT 取得登入者後塞入。
    private Long reporterId;

    private Long reportedUserId;

    private Long postId;

    private Long commentId;

    private String reason;
}