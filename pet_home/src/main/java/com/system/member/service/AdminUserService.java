package com.system.member.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.system.member.dto.request.UpdateRolePermissionRequest;
import com.system.member.dto.request.UpdateUserRoleRequest;
import com.system.member.dto.request.UserStatusRequest;
import com.system.member.dto.response.*;
import com.system.member.entity.*;
import com.system.member.exception.BusinessException;
import com.system.member.repository.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AdminUserService {

    @Autowired
    UsersRepository userRepository;
    @Autowired
    private UserProfileRepository userProfileRepository;
    @Autowired
    private UserRoleRepository userRoleRepository;
    @Autowired
    private PermissionRepository permissionRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private RolePermissionRepository rolePermissionRepository;
    @Autowired
    private AuditLogService auditLogService;
    @Autowired
    private LoginAttemptRepository loginAttemptRepository;
    @Autowired
    private UserSessionService userSessionService;
    @Autowired
    private UserSessionRepository userSessionRepository;
    @Autowired
    private ObjectMapper objectMapper;

    @Transactional(readOnly = true)
    public List<AdminUserResponse> AdminGetUser() {
        List<UserBean> userList = userRepository.findAllByOrderByCreatedDateDesc();

        return userList.stream()
                .map(user -> {
                    UserProfilesBean profile = userProfileRepository.findByUserId(user.getId())
                            .orElse(null);

                    List<String> roles = userRoleRepository.findByUser_Id(user.getId())
                            .stream()
                            .map(userRole -> userRole.getRole().getRoleCode())
                            .toList();

                    return new AdminUserResponse(
                            user.getId(),
                            user.getEmail(),
                            profile != null ? profile.getNickname() : null,
                            user.getStatus(),
                            user.isEmailVerified(),
                            user.getLastLoginAt(),
                            roles
                    );
                })
                .toList();
    }

    @Transactional(readOnly = true)
    public AdminUserDetailResponse AdminGetUserDetail(Long userid) {
        UserBean user = userRepository.findById(userid)
                .orElseThrow(() -> new BusinessException("USER_NOT_FOUND", "找不到使用者"));

        UserProfilesBean profile = userProfileRepository.findByUserId(userid)
                .orElseThrow(() -> new BusinessException("USERPROFILE_NOT_FOUND", "找不到使用者資訊"));

        List<String> roles = userRoleRepository.findByUser_Id(userid)
                .stream().map(userRole -> userRole.getRole().getRoleCode()).toList();

        List<String> permissions = permissionRepository.findPermissionCodesByUserId(user.getId());

        return new AdminUserDetailResponse(
                userid,
                user.getEmail(),
                profile.getNickname(),
                user.getStatus(),
                user.isEmailVerified(),
                user.isTwoFactorEnabled(),
                user.getCreatedDate(),
                user.getLastLoginAt(),
                roles,
                permissions
        );
    }

    @Transactional(readOnly = true)
    public List<LoginAttemptResponse> AdminGetUserLoginAttempt() {
        List<LoginAttemptBean> loginList = loginAttemptRepository.findAllByOrderByAttemptedAtDesc();

        return loginList.stream()
                .map(loginAttempt -> {
                    return new LoginAttemptResponse(
                            loginAttempt.getId(),
                            loginAttempt.getUser() != null ? loginAttempt.getUser().getId() : null, loginAttempt.getEmail(),
                            loginAttempt.getIpAddress(),
                            loginAttempt.getUserAgent(),
                            loginAttempt.getSuccess(),
                            loginAttempt.getFailureReason(),
                            loginAttempt.getAttemptedAt()
                    );
                }).toList();
    }

    @Transactional
    public UpdateUserRoleResponse updateUserRole(
            Long adminId, Long userid, UpdateUserRoleRequest request, HttpServletRequest httpRequest) {

        UserBean user = userRepository.findById(userid)
                .orElseThrow(() -> new BusinessException("USER_NOT_FOUND", "找不到使用者"));

        if (request.getRoles() == null || request.getRoles().isEmpty()) {
            throw new BusinessException("ROLE_REQUIRED", "角色不可為空");
        }

        List<RoleBean> roles = roleRepository.findByRoleCodeIn(request.getRoles());

        if (roles.size() != request.getRoles().size()) {
            throw new BusinessException("ROLE_NOT_FOUND", "包含不存在的角色");
        }

        // 先記錄修改前角色
        List<String> oldRoleCodes = userRoleRepository.findByUser_Id(userid)
                .stream()
                .map(userRole -> userRole.getRole().getRoleCode())
                .toList();

        // 新角色
        List<String> newRoleCodes = roles.stream()
                .map(RoleBean::getRoleCode)
                .toList();

        userRoleRepository.deleteByUser_Id(userid);
        userRoleRepository.flush();

        List<UserRoleBean> userRoles = roles.stream()
                .map(role -> {
                    UserRoleBean userRole = new UserRoleBean();
                    userRole.setUser(user);
                    userRole.setRole(role);
                    return userRole;
                })
                .toList();

        userRoleRepository.saveAll(userRoles);

        user.setPermissionVersion(user.getPermissionVersion() + 1);
        user.setUpdatedDate(LocalDateTime.now());
        userRepository.save(user);

        userSessionService.revokeSession(userid, "ROLE_CHANGE");

        String metadata;

        try {
            metadata = objectMapper.writeValueAsString(Map.of(
                    "userId", userid,
                    "email", user.getEmail(),
                    "oldRoles", oldRoleCodes,
                    "newRoles", newRoleCodes,
                    "permissionVersion", user.getPermissionVersion()
            ));
        } catch (JsonProcessingException e) {
            throw new BusinessException("AUDIT_METADATA_ERROR", "操作紀錄資料建立失敗");
        }

        auditLogService.createAuditLog(
                adminId,
                "ADMIN_UPDATE_USER_ROLE",
                "ADMIN",
                "USER",
                userid,
                metadata,
                httpRequest
        );

        return new UpdateUserRoleResponse(
                user.getId(),
                user.getEmail(),
                newRoleCodes,
                user.getPermissionVersion()
        );
    }

    @Transactional
    public void updateUserStatus(
            Long adminId, Long userid, UserStatusRequest request, HttpServletRequest httpRequest) {
        UserBean user = userRepository.findById(userid)
                .orElseThrow(() -> new BusinessException("USER_NOT_FOUND", "找不到使用者"));

        String oldStatus = user.getStatus();
        LocalDateTime oldStatusUntil = user.getStatusUntil();
        String newStatus = request.getStatus().trim().toUpperCase();

        if (!List.of("ACTIVE", "LOCKED", "SUSPENDED", "BANNED").contains(newStatus)) {
            throw new BusinessException("INVALID_USER_STATUS", "不支援的帳號狀態");
        }

        if (adminId.equals(userid) && !"ACTIVE".equals(newStatus)) {
            throw new BusinessException("CANNOT_DISABLE_SELF", "不可停用自己的管理員帳號");
        }

        if ("LOCKED".equals(newStatus)) {
            if (request.getStatusUntil() == null) {
                throw new BusinessException("STATUS_UNTIL_REQUIRED", "鎖定狀態需要設定結束時間");
            }

            if (!request.getStatusUntil().isAfter(LocalDateTime.now())) {
                throw new BusinessException("STATUS_UNTIL_INVALID", "狀態結束時間必須晚於現在");
            }
        }

        user.setStatus(newStatus);
        if ("ACTIVE".equals(newStatus)) {
            user.setStatusUntil(null);
            user.setFailedLoginCount(0);

        } else {
            user.setStatusUntil(request.getStatusUntil());
        }

        user.setUpdatedDate(LocalDateTime.now());
        user.setTokenVersion(user.getTokenVersion() + 1);
        userRepository.save(user);

        if (!"ACTIVE".equals(newStatus)) {
            userSessionService.revokeSession(userid, "USER_STATUS_CHANGE");
        }

        auditLogService.createAuditLog(
                adminId,
                "ADMIN_UPDATE_USER_STATUS",
                "ADMIN",
                "USER",
                userid,
                """
                        {
                          "oldStatus": "%s",
                          "newStatus": "%s",
                          "oldStatusUntil": "%s",
                          "newStatusUntil": "%s",
                          "reason": "%s"
                        }
                        """.formatted(
                        oldStatus,
                        newStatus,
                        oldStatusUntil,
                        user.getStatusUntil(),
                        request.getReason()
                ),
                httpRequest
        );
    }

    @Transactional(readOnly = true)
    public AdminRolePermissionResponse getRolePermission(Long roleId) {
        RoleBean role = roleRepository.findById(roleId)
                .orElseThrow(() -> new BusinessException("ROLE_NOT_FOUND", "找不到角色"));

        List<String> permissions = rolePermissionRepository.findByRole_Id(roleId)
                .stream().map(permission -> permission.getPermission().getPermissionCode()).toList();

        return new AdminRolePermissionResponse(
                role.getId(),
                role.getRoleCode(),
                role.getRoleName(),
                permissions
        );
    }

    @Transactional
    public void updateRolePermission(Long adminId, Long roleId, UpdateRolePermissionRequest request, HttpServletRequest httpRequest) {
        RoleBean role = roleRepository.findById(roleId)
                .orElseThrow(() -> new BusinessException("ROLE_NOT_FOUND", "找不到角色"));
        List<String> permissionCodes = request.getPermissions();

        if (permissionCodes == null) {
            throw new BusinessException("PERMISSION_REQUIRED", "權限不可為空");
        }

        List<PermissionBean> permissions = permissionRepository.findByPermissionCodeIn(permissionCodes);

        if (permissions.size() != permissionCodes.size()) {
            throw new BusinessException("PERMISSION_NOT_FOUND", "包含不存在的權限");
        }

        List<String> oldPermissions = rolePermissionRepository.findByRole_Id(roleId)
                .stream()
                .map(rolePermission -> rolePermission.getPermission().getPermissionCode())
                .toList();

        rolePermissionRepository.deleteByRole_Id(roleId);
        rolePermissionRepository.flush();

        List<RolePermissionBean> rolePermissions = permissions.stream().map(
                permission -> {
                    RolePermissionBean rolePermission = new RolePermissionBean();
                    rolePermission.setRole(role);
                    rolePermission.setPermission(permission);
                    return rolePermission;
                }).toList();

        rolePermissionRepository.saveAll(rolePermissions);

        userRepository.increasePermissionVersionByRoleId(roleId);
        userSessionService.revokeSessionsByRoleId(roleId, "ROLE_PERMISSION_CHANGED");


        auditLogService.createAuditLog(
                adminId,
                "ADMIN_UPDATE_ROLE_PERMISSION",
                "ADMIN",
                "ROLE",
                roleId,
                """
                        {
                          "roleCode": "%s",
                          "oldPermissions": %s,
                          "newPermissions": %s
                        }
                        """.formatted(
                        role.getRoleCode(),
                        oldPermissions,
                        permissionCodes
                ),
                httpRequest
        );
    }

    @Transactional(readOnly = true)
    public List<AdminPermissionGroupResponse> getPermissionGroups() {
        List<PermissionBean> permissions = permissionRepository.findAllWithModuleOrderByModuleSortOrder();

        Map<ModuleBean, List<PermissionBean>> groupedPermissions = permissions.stream()
                .collect(Collectors.groupingBy(
                        PermissionBean::getModule,
                        LinkedHashMap::new,
                        Collectors.toList()
                ));

        return groupedPermissions.entrySet()
                .stream()
                .map(entry -> {
                    ModuleBean module = entry.getKey();

                    List<AdminPermissionGroupResponse.PermissionItem> permissionItems =
                            entry.getValue()
                                    .stream()
                                    .map(permission -> new AdminPermissionGroupResponse.PermissionItem(
                                            permission.getPermissionCode(),
                                            permission.getPermissionName()
                                    ))
                                    .toList();

                    return new AdminPermissionGroupResponse(
                            module.getModuleCode(),
                            module.getModuleName(),
                            module.getSortOrder(),
                            permissionItems
                    );
                })
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AdminRolesResponse> getRoles() {
        return roleRepository.findAll().stream()
                .map(role -> new AdminRolesResponse(role.getId(), role.getRoleCode(), role.getRoleName()))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AdminSessionResponse> getAllSessions() {
        List<UserSessionBean> sessions = userSessionRepository.findAllByOrderByCreatedDateDesc();

        return sessions.stream()
                .map(this::toAdminSessionResponse)
                .toList();
    }

    private AdminSessionResponse toAdminSessionResponse(UserSessionBean session) {
        UserBean user = session.getUser();

        return new AdminSessionResponse(
                session.getId(),
                user.getId(),
                user.getEmail(),
                session.getIpAddress(),
                session.getIsActive(),
                session.getLoginAt(),
                session.getLastUsedAt(),
                session.getExpiresAt(),
                session.getRevokedAt(),
                session.getRevokedReason()
        );
    }
}
