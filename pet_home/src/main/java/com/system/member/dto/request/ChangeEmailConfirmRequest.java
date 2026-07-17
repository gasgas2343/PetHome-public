package com.system.member.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ChangeEmailConfirmRequest {
    @NotBlank(message = "驗證 token 不可為空")
    private String token;
}
