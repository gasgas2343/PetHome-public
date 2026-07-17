package com.system.member.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ChangeEmailRequest {

    @NotBlank(message = "新電子信箱不可為空")
    @Email(message = "電子信箱格式錯誤")
    private String newEmail;
}
