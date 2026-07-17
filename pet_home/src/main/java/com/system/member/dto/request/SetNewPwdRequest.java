package com.system.member.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SetNewPwdRequest {
    @Size(min = 6, message = "密碼至少需為6碼")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$",
            message = "密碼須包含大小寫字母和數字")
    @NotBlank(message = "請輸入新密碼")
    private String password;

    @NotBlank(message = "請再次輸入新密碼")
    private String confirmPassword;

    @NotBlank
    private String resetToken;
}
