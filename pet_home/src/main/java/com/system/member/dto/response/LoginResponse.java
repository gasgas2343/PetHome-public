package com.system.member.dto.response;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private Long id;
    private String email;
    private String nickName;
    private String avatarUrl;
    private List<String> roles;

//    正式登入成功後才會有值
    private String accessToken;
    private String refreshToken;
    private LocalDateTime accessTokenExpiresIn;
    private LocalDateTime refreshTokenExpiresIn;

//    是否需要兩步驟驗證
    private Boolean requiresTwoFactor;

//    只有 requiresTwoFactor = true 時才會有值
    private String loginChallengeToken;


}
