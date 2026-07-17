package com.system.member.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TotpCloseRequest {
    @NotBlank(message = "驗證碼不可為空")
    private String code;
}
