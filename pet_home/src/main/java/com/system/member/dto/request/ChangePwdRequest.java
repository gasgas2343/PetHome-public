package com.system.member.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ChangePwdRequest {
    @NotBlank(message = "請輸入舊密碼")
    private String oldPwd;

    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$",
            message = "密碼須包含大小寫字母和數字")
    @NotBlank(message = "請輸入新密碼")
    private String newPwd;

    @NotBlank(message = "請再次輸入新密碼")
    private String confirmPwd;

    @NotBlank(message = "缺少 refresh token")
    private String refreshToken;
}
