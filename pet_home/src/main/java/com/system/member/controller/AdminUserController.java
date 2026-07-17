package com.system.member.controller;

import com.system.member.dto.ApiResponse;
import com.system.member.dto.request.UpdateRolePermissionRequest;
import com.system.member.dto.request.UpdateUserRoleRequest;
import com.system.member.dto.request.UserStatusRequest;
import com.system.member.dto.response.*;
import com.system.member.security.annotation.RequirePermission;
import com.system.member.service.AdminUserService;
import com.system.member.service.AuditLogService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminUserController {

    @Autowired
    private AdminUserService adminUserService;
    @Autowired
    private AuditLogService auditLogService;

    @RequirePermission("ADMIN_USER_READ")
    @GetMapping("/users")
    public ApiResponse<List<AdminUserResponse>> getAllUsers() {
        List<AdminUserResponse> response = adminUserService.AdminGetUser();

        return ApiResponse.success("ADMIN_USER_LIST_SUCCESS", "取得使用者列表成功", response);
    }

    @RequirePermission("ADMIN_USER_DETAIL_READ")
    @GetMapping("/users/{id}")
    public ApiResponse<AdminUserDetailResponse> getUserDetail(@PathVariable Long id) {
        AdminUserDetailResponse response = adminUserService.AdminGetUserDetail(id);

        return ApiResponse.success("ADMIN_USER_DETAIL_SUCCESS", "取得使用者詳細資料成功", response);
    }

    @RequirePermission("ADMIN_USER_UPDATE")
    @PutMapping("/users/roles/{userId}")
    public ApiResponse<UpdateUserRoleResponse> updateUserRole(@PathVariable Long userId, @RequestBody UpdateUserRoleRequest request,
                                                              HttpServletRequest httpRequest, Authentication authentication) {
        Long adminId = Long.valueOf(authentication.getName());
        UpdateUserRoleResponse response = adminUserService.updateUserRole(adminId, userId, request, httpRequest);

        return ApiResponse.success("ADMIN_USER_ROLES_UPDATE_SUCCESS", "使用者角色更新成功", response);
    }

    @RequirePermission("RBAC_ROLE_READ")
    @GetMapping("/roles")
    public ApiResponse<List<AdminRolesResponse>> getRoles() {
        List<AdminRolesResponse> responses = adminUserService.getRoles();

        return ApiResponse.success("RBAC_ROLE_LIST_SUCCESS", "取得所有角色列表成功", responses);
    }

    @RequirePermission("RBAC_PERMISSION_READ")
    @GetMapping("/permissions")
    public ApiResponse<List<AdminPermissionGroupResponse>> getPermissionGroups() {
        List<AdminPermissionGroupResponse> response = adminUserService.getPermissionGroups();
        return ApiResponse.success("RBAC_PERMISSIONS_LIST_SUCCESS", "取得所有角色權限列表成功", response);
    }

    @RequirePermission("RBAC_PERMISSION_READ")
    @GetMapping("/permissions/{id}")
    public ApiResponse<AdminRolePermissionResponse> getModule(@PathVariable Long id) {
        AdminRolePermissionResponse response = adminUserService.getRolePermission(id);

        return ApiResponse.success("RBAC_ROLE_READ_SUCCESS", "取得角色權限列表成功", response);
    }

    @RequirePermission("RBAC_ROLE_UPDATE")
    @PutMapping("/permissions/{id}")
    public ApiResponse<Void> updateRolePermissions(Authentication authentication, @PathVariable Long id,
                                                   @RequestBody UpdateRolePermissionRequest request, HttpServletRequest httpRequest) {
        Long adminId = Long.valueOf(authentication.getName());
        adminUserService.updateRolePermission(adminId, id, request, httpRequest);

        return ApiResponse.success("RBAC_ROLE_UPDATE_SUCCESS", "更改角色權限列表成功", null);
    }

    @RequirePermission("ADMIN_USER_UPDATE")
    @PutMapping("/users/status/{id}")
    public ApiResponse<Void> updateUserStatus(Authentication authentication, @PathVariable Long id,
                                              @RequestBody @Valid UserStatusRequest request, HttpServletRequest httpRequest) {
        Long adminId = Long.valueOf(authentication.getName());
        adminUserService.updateUserStatus(adminId, id, request, httpRequest);

        return ApiResponse.success("ADMIN_USER_STATUS_UPDATE_SUCCESS", "會員狀態更新成功", null);
    }

    @RequirePermission("ADMIN_AUDIT_READ")
    @GetMapping("/logs")
    public ApiResponse<List<AuditLogResponse>> getAllLogs() {
        List<AuditLogResponse> responses = auditLogService.getAuditLogs();

        return ApiResponse.success("AUDIT_LOG_LIST_SUCCESS", "取得操作列表成功", responses);
    }

    @RequirePermission("ADMIN_LOGIN_ATTEMPT_READ")
    @GetMapping("/login-attempts")
    public ApiResponse<List<LoginAttemptResponse>> getLoginAttempts() {
        List<LoginAttemptResponse> response = adminUserService.AdminGetUserLoginAttempt();

        return ApiResponse.success("LOGIN_ATTEMPTS_LIST_SUCCESS", "取得登入列表成功", response);
    }

    @RequirePermission("ADMIN_LOGIN_ATTEMPT_READ")
    @GetMapping("/sessions")
    public ApiResponse<List<AdminSessionResponse>> getAllSessions() {
        List<AdminSessionResponse> response = adminUserService.getAllSessions();

        return ApiResponse.success("ADMIN_SESSION_LIST_SUCCESS", "取得登入 Session 列表成功", response);
    }
}
