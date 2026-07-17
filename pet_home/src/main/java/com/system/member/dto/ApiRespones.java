package com.system.member.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ApiRespones<T> {
    private boolean success;
    private String code;
    private String message;
    private T data;

    public static <T> ApiRespones<T> success(String code, String message, T data) {
      return new ApiRespones<>(true, code, message, data);
    }

    public static <T> ApiRespones<T> error(String code, String message) {
        return new ApiRespones<>(false, code, message, null);
    }
    public static <T> ApiRespones<T> error(String code, String message, T data) {
        return new ApiRespones<>(false, code, message, data);
    }
}
