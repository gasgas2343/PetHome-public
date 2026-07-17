package com.system.member.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TotpConfirmRequest {
    @NotBlank(message = "驗證碼不可為空")
    @Pattern(regexp = "\\d{6}", message = "驗證碼必須是 6 位數字")
    private String code;
}
