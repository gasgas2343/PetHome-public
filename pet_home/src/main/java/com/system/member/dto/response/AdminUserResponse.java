package com.system.member.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class AdminUserResponse {
    private Long id;
    private String email;
    private String nickName;
    private String status;
    private boolean emailVerified;
    private LocalDateTime lastLoginAt;
    private List<String> roles;
}
