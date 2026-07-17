package com.system.shop.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Result<T> {
    private Integer code;
    private String message;
    private T data;

    // 成功時呼叫
    public static <T> Result<T> success(T data) {
        return new Result<>(200, "操作成功", data);
    }

    // 完整的錯誤呼叫
    public static <T> Result<T> error(Integer code, String message) {
        return new Result<>(code, message, null);
    }

    // 新增：僅傳入訊息的錯誤呼叫 (預設代碼為 500)
    public static <T> Result<T> error(String message) {
        return new Result<>(500, message, null);
    }
}
