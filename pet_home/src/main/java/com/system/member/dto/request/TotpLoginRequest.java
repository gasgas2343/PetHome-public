package com.system.member.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class TotpLoginRequest {
    @NotBlank(message = "登入驗證 token 不可為空")
    private String loginChallengeToken;

    @NotBlank(message = "驗證碼不可為空")
    private String code;
}
