package com.system.petpost.exception;

import java.time.LocalDateTime;

import lombok.Data;

// 錯誤回應DTO
//
// 功能：
// 1. 統一API錯誤格式
// 2. 提供錯誤時間
// 3. 提供錯誤狀態碼
// 4. 提供錯誤訊息
@Data
public class ErrorResponse {

    // 發生時間
    private LocalDateTime timestamp;

    // HTTP狀態碼
    private Integer status;

    // 錯誤訊息
    private String message;

    // {
    // "timestamp": "...",
    // "status": 404,
    // "error": "Not Found",
    // "message": "文章不存在",
    // "path": "/api/posts/100"
    // }
    private String error;

    private String path;
}