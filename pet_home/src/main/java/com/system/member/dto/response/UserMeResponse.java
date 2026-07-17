package com.system.member.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class UserMeResponse {
    Long id;
    private String email;
    private String avatarUrl;
    private String nickName;
    private String status;
    private boolean emailVerified;
    private boolean twoFactorEnabled;
    private List<String> roles;
}
