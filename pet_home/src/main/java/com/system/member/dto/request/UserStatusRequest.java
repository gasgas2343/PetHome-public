package com.system.member.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class UserStatusRequest {

    @NotBlank(message = "狀態不可為空")
    private String status;
    private LocalDateTime statusUntil;
    private String reason;
}
