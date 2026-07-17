package com.system.member.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class AdminRolePermissionResponse {
    private Long id;
    private String roleCode;
    private String roleName;
    private List<String> permissions;
}
