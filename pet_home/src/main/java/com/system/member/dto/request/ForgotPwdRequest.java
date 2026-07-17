package com.system.member.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ForgotPwdRequest {
    @NotBlank(message = "Email不可為空")
    @Pattern(regexp = "^[a-zA-Z0-9+-_.]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}",
            message = "Email格式有誤")
    private String email;
}
