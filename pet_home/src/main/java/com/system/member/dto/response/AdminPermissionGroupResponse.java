package com.system.member.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@AllArgsConstructor
public class AdminPermissionGroupResponse {
    private String moduleCode;
    private String moduleName;
    private Integer sortOrder;
    private List<PermissionItem> permissions;


    @Getter
    @Setter
    @AllArgsConstructor
    public static class PermissionItem {
        private String permissionCode;
        private String permissionName;
    }
}
