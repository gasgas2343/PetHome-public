package com.system.member.exception;

import com.system.member.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> BusinessException(BusinessException ex){
        ApiResponse<Void> response = ApiResponse.error(
                ex.getCode(),
                ex.getMessage()
        );
        return ResponseEntity
                .status(ex.getStatus())
                .body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> validationNotValid(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            String fieldName = fieldError.getField();
            String errorMessage = fieldError.getDefaultMessage();

            if (!errors.containsKey(fieldName)) {
                errors.put(fieldName, errorMessage);
            }
        }
        ApiResponse<Map<String, String>> response = ApiResponse.error(
                "VALIDATION_FAILED",
                "欄位驗證錯誤",
                errors
        );

        return ResponseEntity.badRequest().body(response);
    }
}
