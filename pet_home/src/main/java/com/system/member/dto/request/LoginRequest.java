package com.system.member.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LoginRequest {
    @NotBlank(message = "Email不可為空")
    @Pattern(regexp = "^[a-zA-Z0-9+-_.]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}",
            message = "Email格式有誤")
    private String email;
    @NotBlank(message = "密碼不可為空")
    @Size(min = 6, message = "密碼至少需為6碼")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$",
            message = "密碼須包含大小寫字母和數字")
    private String password;
}
