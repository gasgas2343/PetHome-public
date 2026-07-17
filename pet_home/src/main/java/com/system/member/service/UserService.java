package com.system.member.service;

import com.system.member.dto.request.ChangeEmailRequest;
import com.system.member.dto.request.ChangePwdRequest;
import com.system.member.dto.response.RefreshTokenResponse;
import com.system.member.dto.response.UserMeResponse;
import com.system.member.dto.response.UserModuleResponse;
import com.system.member.entity.*;
import com.system.member.exception.BusinessException;
import com.system.member.repository.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UsersRepository usersRepository;
    @Autowired
    private UserProfileRepository userProfileRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserSessionRepository userSessionRepository;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private UserRoleRepository userRoleRepository;
    @Autowired
    private RolePermissionRepository rolePermissionRepository;
    @Autowired
    private AuditLogService auditLogService;
    @Autowired
    private EmailVerificationTokenRepository emailVerificationTokenRepository;
    @Autowired
    private MailService mailService;
    @Autowired
    private UserSessionService userSessionService;

    @Transactional(readOnly = true)
    public UserMeResponse getMe(Long id) {
        UserBean user = usersRepository.findById(id)
                .orElseThrow(() -> new BusinessException("USER_NOT_FOUND", "找不到使用者"));

        UserProfilesBean profile = userProfileRepository.findByUserId(id)
                .orElseThrow(() -> new BusinessException("USERPROFILE_NOT_FOUND", "找不到使用者資訊"));

        List<String> roles = userRoleRepository.findByUser_Id(user.getId())
                .stream().map(userRole -> userRole.getRole().getRoleCode()).toList();

        return new UserMeResponse(
                user.getId(),
                user.getEmail(),
                profile.getAvatarUrl(),
                profile.getNickname(),
                user.getStatus(),
                user.isEmailVerified(),
                user.isTwoFactorEnabled(),
                roles
        );
    }

    @Transactional(readOnly = true)
    public UserModuleResponse getModule(Long id) {
        List<Long> roleIds = userRoleRepository.findByUser_Id(id)
                .stream().map(role -> role.getRole().getId()).toList();

        if (roleIds.isEmpty()) {
            return new UserModuleResponse(List.of());
        }

        List<RolePermissionBean> rolePermissions =
                rolePermissionRepository.findByRoleIdsWithPermissionAndModule(roleIds);

        List<PermissionBean> permissions = rolePermissions.stream()
                .map(RolePermissionBean::getPermission).distinct().toList();

        Map<String, List<PermissionBean>> groupedByModule = permissions.stream()
                .collect(Collectors.groupingBy(permission -> permission.getModule().getModuleCode()));

        List<UserModuleResponse.ModuleItem> modules = groupedByModule.values()
                .stream()
                .map(permissionList -> {
                    PermissionBean firstPermission = permissionList.get(0);
                    ModuleBean module = firstPermission.getModule();

                    List<String> permissionCodes = permissionList.stream()
                            .map(PermissionBean::getPermissionCode)
                            .distinct()
                            .toList();

                    return new UserModuleResponse.ModuleItem(
                            module.getModuleCode(),
                            module.getModuleName(),
                            module.getSortOrder(),
                            permissionCodes
                    );
                })
                .sorted(Comparator.comparing(UserModuleResponse.ModuleItem::getSortOrder))
                .toList();

        return new UserModuleResponse(modules);
    }

    @Transactional
    public RefreshTokenResponse changePassword(Long id, ChangePwdRequest request, HttpServletRequest httpRequest) {
        UserBean user = usersRepository.findById(id)
                .orElseThrow(() -> new BusinessException("USER_NOT_FOUND", "找不到使用者"));

        if (!passwordEncoder.matches(request.getOldPwd(), user.getPasswordHash())) {
            throw new BusinessException("OLD_PASSWORD_INVALID", "舊密碼不正確");
        }
        if (passwordEncoder.matches(request.getNewPwd(), user.getPasswordHash())) {
            throw new BusinessException("PASSWORD_SAME_AS_OLD", "新密碼不能與舊密碼相同");
        }
        if (!request.getNewPwd().equals(request.getConfirmPwd())) {
            throw new BusinessException("PASSWORD_CONFIRM_MISMATCH", "新密碼與確認密碼不一致");
        }

        LocalDateTime now = LocalDateTime.now();
        String newPwdHash = passwordEncoder.encode(request.getNewPwd());
        String oldRefreshToken = jwtService.hashSecureToken(request.getRefreshToken());

        UserSessionBean session = userSessionRepository.findByRefreshTokenHashAndIsActiveTrue(oldRefreshToken)
                .orElseThrow(() -> new BusinessException("SESSION_NOT_FOUND", "找不到有效登入紀錄"));

        if (!session.getUser().getId().equals(id)) {
            throw new BusinessException("SESSION_USER_MISMATCH", "登入狀態不一致");
        }

        user.setPasswordHash(newPwdHash);
        user.setTokenVersion(user.getTokenVersion() + 1);
        user.setUpdatedDate(now);
        usersRepository.save(user);

        String newAccessToken = jwtService.getAccessToken(user);
        String newRefreshToken = jwtService.generateSecureToken();
        String newRefreshTokenHash = jwtService.hashSecureToken(newRefreshToken);
        LocalDateTime newAccessTokenExpireAt = jwtService.getAccessTokenExpireAt();
        LocalDateTime newRefreshTokenExpireAt = jwtService.getSecureTokenExpireAt();

        userSessionService.revokeSession(user.getId(), "PASSWORD_CHANGE");

        session.setRefreshTokenHash(newRefreshTokenHash);
        session.setLastUsedAt(now);
        session.setExpiresAt(newRefreshTokenExpireAt);
        session.setUpdatedDate(now);
        userSessionRepository.save(session);



        auditLogService.createAuditLog(
                user.getId(),
                " USER_CHANGE_PASSWORD",
                "MEMBER",
                "USER",
                user.getId(),
                "{\"reason\":\"PASSWORD_CHANGE\",\"revokedOtherSessions\":true}",
                httpRequest
        );

        return new RefreshTokenResponse(
                newAccessToken, newRefreshToken,
                newAccessTokenExpireAt, newRefreshTokenExpireAt
        );
    }

    @Transactional
    public void changeEmail(Long id, ChangeEmailRequest request, HttpServletRequest httpRequest) {
        UserBean user = usersRepository.findById(id)
                .orElseThrow(() -> new BusinessException("USER_NOT_FOUND", "找不到使用者"));

        if (user.getEmail().equals(request.getNewEmail())) {
            throw new BusinessException("EMAIL_SAME_AS_CURRENT", "新信箱不可與目前信箱相同");
        }

        if (usersRepository.existsByEmail(request.getNewEmail())) {
            throw new BusinessException("EMAIL_ALREADY_USED", "此電子信箱已被使用");
        }

        if (usersRepository.existsByPendingEmail(request.getNewEmail())) {
            throw new BusinessException("EMAIL_CHANGE_PENDING", "此電子信箱正在等待驗證");
        }

        LocalDateTime now = LocalDateTime.now();
        user.setPendingEmail(request.getNewEmail());
        user.setUpdatedDate(now);
        usersRepository.save(user);

        String token = jwtService.generateSecureToken();
        String tokenHash = jwtService.hashSecureToken(token);
        LocalDateTime tokenExpireAt = jwtService.getSecureTokenExpireAt();

        EmailVerificationTokenBean verification = emailVerificationTokenRepository.findByUser_IdAndVerificationType(user.getId(), "CHANGE_EMAIL")
                .orElse(new EmailVerificationTokenBean());

        verification.setUser(user);
        verification.setTokenHash(tokenHash);
        verification.setVerificationType("CHANGE_EMAIL");
        verification.setExpiresAt(tokenExpireAt);
        verification.setUsedAt(null);
        emailVerificationTokenRepository.save(verification);

        mailService.sendEmailChangeConfirm(user.getPendingEmail(), token);

        auditLogService.createAuditLog(
                user.getId(),
                "EMAIL_CHANGE_REQUESTED",
                "MEMBER",
                "USER",
                user.getId(),
                "{\"oldEmail\":\"" + user.getEmail() + "\",\"pendingEmail\":\"" + request.getNewEmail() + "\"}",
                httpRequest
        );
    }
}

