package com.system.member.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class GetUserProfileResponse {
    private Long id;
    private String email;
    private Boolean emailVerified;
    private String fullName;
    private String nickName;
    private String phone;
    private LocalDate birthday;
    private String avatarUrl;
    private Integer pointsBalance;
}
