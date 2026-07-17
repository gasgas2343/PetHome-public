package com.system.member.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class UpdateUserRoleResponse {
    private Long id;
    private String email;
    private List<String> roles;
    private Integer getPermissionVersion;
}
