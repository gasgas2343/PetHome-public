package com.system.member.service;

import com.system.member.dto.request.*;
import com.system.member.dto.response.LoginResponse;
import com.system.member.dto.response.RefreshTokenResponse;
import com.system.member.dto.response.RegisterResponse;
import com.system.member.dto.response.UserMeResponse;
import com.system.member.entity.*;
import com.system.member.exception.BusinessException;
import com.system.member.repository.*;
import com.system.member.util.CryptoUtil;
import com.system.member.util.TotpUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
public class AuthService {
    @Autowired
    private UsersRepository usersRepository;
    @Autowired
    private UserProfileRepository userProfileRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private UserSessionService userSessionService;
    @Autowired
    private UserSessionRepository userSessionRepository;
    @Autowired
    private LoginAttemptService loginAttemptService;
    @Autowired
    private PasswordResetTokensRepository passwordResetTokensRepository;
    @Autowired
    private MailService mailService;
    @Autowired
    private EmailVerificationTokenRepository emailVerificationTokenRepository;
    @Autowired
    private UserRoleRepository userRoleRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private UserNotificationService userNotificationService;
    @Autowired
    private UserTwoFactorSettingRepository userTwoFactorSettingRepository;
    @Autowired
    private CryptoUtil cryptoUtil;
    @Autowired
    private AuditLogService auditLogService;

    @Transactional
    public RegisterResponse register(RegisterRequest registerRequest) {
        if (!registerRequest.getPassword().equals(registerRequest.getPasswordConfirm())) {
            throw new BusinessException("PASSWORD_MISMATCH", "兩次輸入密碼不一致");
        }

        if (usersRepository.existsByEmail(registerRequest.getEmail())) {
            throw new BusinessException("EMAIL_ALREADY_EXISTS", "此Email已被註冊", HttpStatus.CONFLICT);
        }

        String passwordHash = passwordEncoder.encode(registerRequest.getPassword());

        UserProfilesBean userProfiles = new UserProfilesBean();
        UserBean user = new UserBean();

        RoleBean roleBean = roleRepository.findByRoleCode("USER")
                .orElseThrow(() -> new BusinessException("DEFAULT_ROLE_NOT_FOUND", "找不到預設角色"));

        user.setEmail(registerRequest.getEmail());
        user.setPasswordHash(passwordHash);
        UserBean saveUser = usersRepository.save(user);

        userProfiles.setUser(saveUser);
        userProfiles.setNickname(registerRequest.getNickname());
        userProfiles.setAvatarUrl("/images/default-avatar.png");
        userProfileRepository.save(userProfiles);

        UserRoleBean userRole = new UserRoleBean();
        userRole.setUser(saveUser);
        userRole.setRole(roleBean);
        userRoleRepository.save(userRole);

        EmailVerificationTokenBean tokenBean = emailVerificationTokenRepository.findByUser_IdAndVerificationType(user.getId(), "REGISTER_VERIFY")
                .orElse(new EmailVerificationTokenBean());

        LocalDateTime now = LocalDateTime.now();
        String token = jwtService.generateSecureToken();
        String tokenHash = jwtService.hashSecureToken(token);
        LocalDateTime tokenExpireAt = now.plusMinutes(30);

        tokenBean.setUser(user);
        tokenBean.setTokenHash(tokenHash);
        tokenBean.setVerificationType("REGISTER_VERIFY");
        tokenBean.setExpiresAt(tokenExpireAt);
        tokenBean.setUsedAt(null);
        emailVerificationTokenRepository.save(tokenBean);

        mailService.sendEmailVerified(user.getEmail(), token);
        userNotificationService.userProfileNotification(user);

        return new RegisterResponse(
                saveUser.getId(),
                saveUser.getEmail(),
                userProfiles.getNickname(),
                userProfiles.getAvatarUrl()
        );
    }

    @Transactional(noRollbackFor = BusinessException.class)
    public LoginResponse login(LoginRequest loginRequest, HttpServletRequest HttpRequest) {
        UserBean user = usersRepository.findByEmail(loginRequest.getEmail())
                .orElse(null);

        if (user == null) {
            loginAttemptService.createFailureRecord(loginRequest.getEmail(), HttpRequest, "USER_NOT_FOUND");
            throw new BusinessException("AUTH_INVALID", "帳號或密碼錯誤");
        }

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPasswordHash())) {
            passwordFailedAttempt(user, HttpRequest);
            throw new BusinessException("AUTH_INVALID", "帳號或密碼錯誤");
        }

        switch (user.getStatus()) {
            case "ACTIVE":
                break;
            case "PENDING":
                loginAttemptService.createFailureRecord(loginRequest.getEmail(), HttpRequest, "ACCOUNT_PENDING");
                throw new BusinessException("ACCOUNT_PENDING", "帳號尚未啟用", HttpStatus.FORBIDDEN);
            case "LOCKED":
                if (user.getStatusUntil() != null && user.getStatusUntil().isAfter(LocalDateTime.now())) {
                    loginAttemptService.createFailureRecord(loginRequest.getEmail(), HttpRequest, "ACCOUNT_LOCKED");
                    throw new BusinessException("ACCOUNT_LOCKED", "帳號已鎖定，請稍後再試", HttpStatus.FORBIDDEN);
                }
                user.setStatus("ACTIVE");
                user.setStatusUntil(null);
                user.setFailedLoginCount(0);
                user.setUpdatedDate(LocalDateTime.now());
                usersRepository.save(user);
                break;
//          case "SUSPENDED":
//                loginAttemptService.createFailureRecord(loginRequest.getEmail(), HttpRequest, "ACCOUNT_SUSPENDED");
//                throw new BusinessException("ACCOUNT_SUSPENDED", "帳號已被停權", HttpStatus.FORBIDDEN);
            default:
                loginAttemptService.createFailureRecord(loginRequest.getEmail(), HttpRequest, "ACCOUNT_INACTIVE");
                throw new BusinessException("ACCOUNT_INACTIVE", "帳號目前無法使用", HttpStatus.FORBIDDEN);
        }

        if (user.isTwoFactorEnabled()) {
            return LoginResponse.builder()
                    .requiresTwoFactor(true)
                    .loginChallengeToken(jwtService.getLoginChallengeToken(user.getId()))
                    .build();
        }

        LocalDateTime now = LocalDateTime.now();
        user.setLastLoginAt(now);
        user.setUpdatedDate(now);
        user.setFailedLoginCount(0);
        usersRepository.save(user);

        loginAttemptService.createSuccessRecord(user, HttpRequest);
        return issueLoginToken(user, HttpRequest);
    }

    @Transactional
    public LoginResponse totpLogin(TotpLoginRequest request, HttpServletRequest HttpRequest) {
        Long userId = jwtService.getUserId(request.getLoginChallengeToken());

        UserBean user = usersRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("USER_NOT_FOUND", "找不到使用者"));

        if (!user.isTwoFactorEnabled()) {
            throw new BusinessException("TOTP_NOT_ENABLED", "尚未啟用兩步驟驗證");
        }

        UserTwoFactorSettingBean setting = userTwoFactorSettingRepository.findByUser_IdAndMethod(userId, "TOTP")
                .orElseThrow(() -> new BusinessException("TOTP_NOT_ENABLED", "尚未啟用兩步驟驗證"));

        String secret = cryptoUtil.decrypt(setting.getSecretEncrypted());
        Boolean valid = TotpUtil.verifyTotp(secret, request.getCode());

        if (!valid) {
            loginAttemptService.createFailureRecord(user.getEmail(), HttpRequest, "INVALID_TOTP_CODE");
            throw new BusinessException("TOTP_INVALID", "驗證碼錯誤");
        }

        LocalDateTime now = LocalDateTime.now();
        setting.setLastUsedAt(now);
        setting.setUpdatedDate(now);
        userTwoFactorSettingRepository.save(setting);

        user.setLastLoginAt(now);
        user.setUpdatedDate(now);
        usersRepository.save(user);

        loginAttemptService.createSuccessRecord(user, HttpRequest);
        return issueLoginToken(user, HttpRequest);
    }

    @Transactional
    public void logout(RefreshTokenRequest request) {
        String refreshTokenHash = jwtService.hashSecureToken(request.getRefreshToken());
        UserSessionBean session = userSessionRepository.findByRefreshTokenHashAndIsActiveTrue(refreshTokenHash)
                .orElseThrow(() -> new BusinessException("SESSION_NOT_FOUND", "找不到有效登入紀錄"));

        LocalDateTime now = LocalDateTime.now();
        session.setIsActive(false);
        session.setLastUsedAt(now);
        session.setRevokedAt(now);
        session.setRevokedReason("USER_LOGOUT");
        session.setUpdatedDate(now);
        userSessionRepository.save(session);
    }

    @Transactional
    public void sendPwdResetToken(ForgotPwdRequest request, HttpServletRequest httpRequest) {
        Optional<UserBean> optionalUser = usersRepository.findByEmail(request.getEmail());
        if (optionalUser.isEmpty()) return;
        UserBean user = optionalUser.get();

        String resetToken = jwtService.generateSecureToken();
        String resetTokenHash = jwtService.hashSecureToken(resetToken);
        LocalDateTime resetTokenExpireAt = LocalDateTime.now().plusMinutes(15);

        PasswordResetTokenBean tokenBean = new PasswordResetTokenBean();

        tokenBean.setUser(user);
        tokenBean.setTokenHash(resetTokenHash);
        tokenBean.setExpiresAt(resetTokenExpireAt);
        tokenBean.setUsedAt(null);
        passwordResetTokensRepository.save(tokenBean);

        mailService.sendPasswordEmail(user.getEmail(), resetToken);

        auditLogService.createAuditLog(
                user.getId(),
                "PASSWORD_RESET_REQUESTED",
                "MEMBER",
                "USER",
                user.getId(),
                null,
                httpRequest
        );
    }

    @Transactional
    public void setNewPwd(SetNewPwdRequest request, HttpServletRequest httpRequest) {

        LocalDateTime now = LocalDateTime.now();
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new BusinessException("PASSWORD_MISMATCH", "兩次輸入密碼不一致");
        }

        String resetTokenHash = jwtService.hashSecureToken(request.getResetToken());
        PasswordResetTokenBean token = passwordResetTokensRepository.findByTokenHash(resetTokenHash)
                .orElseThrow(() -> new BusinessException("PASSWORD_RESET_TOKEN_INVALID", "密碼重設連結無效或已過期，請重新申請"));

        if (token.getUsedAt() != null) {
            throw new BusinessException("PASSWORD_RESET_TOKEN_INVALID", "密碼重設連結無效或已過期，請重新申請");
        }
        if (token.getExpiresAt().isBefore(now)) {
            throw new BusinessException("PASSWORD_RESET_TOKEN_INVALID", "密碼重設連結無效或已過期，請重新申請");
        }

        UserBean user = usersRepository.findById(token.getUser().getId())
                .orElseThrow(() -> new BusinessException("PASSWORD_RESET_TOKEN_INVALID", "密碼重設連結無效或已過期，請重新申請"));


        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setTokenVersion(user.getTokenVersion() + 1);
        user.setUpdatedDate(now);
        usersRepository.save(user);
        token.setUsedAt(now);
        passwordResetTokensRepository.save(token);

        userSessionService.revokeSession(user.getId(), "PASSWORD_RESET");

        auditLogService.createAuditLog(
                user.getId(),
                "PASSWORD_RESET_SUCCESS",
                "MEMBER",
                "USER",
                user.getId(),
                null,
                httpRequest
        );
    }

    @Transactional
    public RefreshTokenResponse refreshToken(RefreshTokenRequest refreshTokenRequest, HttpServletRequest request) {
        String refreshToken = refreshTokenRequest.getRefreshToken();
        String refreshTokenHash = jwtService.hashSecureToken(refreshToken);

        UserSessionBean oldSession = userSessionRepository.findByRefreshTokenHash(refreshTokenHash)
                .orElseThrow(() -> new BusinessException("INVALID_REFRESH_TOKEN", "Refresh Token 無效"));

        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);

        if (!oldSession.getIsActive()) {
            throw new BusinessException("SESSION_REVOKED", "登入狀態已失效");
        }
        if (oldSession.getRevokedAt() != null) {
            throw new BusinessException("SESSION_REVOKED", "登入狀態已失效");
        }
        if (oldSession.getExpiresAt().isBefore(now)) {
            throw new BusinessException("REFRESH_TOKEN_EXPIRED", "登入狀態已過期，請重新登入");
        }
        UserBean user = oldSession.getUser();
        if (!"ACTIVE".equals(user.getStatus())) {
            throw new BusinessException("USER_STATUS_INVALID", "帳號狀態不可使用");
        }

        oldSession.setIsActive(false);
        oldSession.setRevokedAt(now);
        oldSession.setLastUsedAt(now);
        oldSession.setRevokedReason("REFRESH_ROTATED");
        oldSession.setUpdatedDate(now);
        userSessionRepository.save(oldSession);

        String accessToken = jwtService.getAccessToken(user);
        LocalDateTime accessTokenExpireAt = jwtService.getAccessTokenExpireAt();
        String refreshTokenNew = jwtService.generateSecureToken();
        String refreshTokenHashNew = jwtService.hashSecureToken(refreshTokenNew);
        LocalDateTime refreshTokenExpireAt = jwtService.getSecureTokenExpireAt();
        String ipAddress = getClientIp(request);
        String userAgent = request.getHeader("User-Agent");

        userSessionService.createSession(user, refreshTokenHashNew,
                ipAddress, userAgent, refreshTokenExpireAt);

        return new RefreshTokenResponse(
                accessToken, refreshTokenNew,
                accessTokenExpireAt, refreshTokenExpireAt);
    }

    @Transactional
    public void requestEmailVerified(SendVerificationEmailRequest request) {
        UserBean user = usersRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BusinessException("USER_NOT_FOUND", "找不到使用者"));

        if (user.isEmailVerified()) {
            throw new BusinessException("EMAIL_ALREADY_VERIFIED", "Email 已經完成驗證");
        }

        EmailVerificationTokenBean tokenBean = emailVerificationTokenRepository.findByUser_IdAndVerificationType(user.getId(), "REGISTER_VERIFY")
                .orElse(new EmailVerificationTokenBean());

        LocalDateTime now = LocalDateTime.now();
        String token = jwtService.generateSecureToken();
        String tokenHash = jwtService.hashSecureToken(token);
        LocalDateTime tokenExpireAt = now.plusMinutes(30);

        tokenBean.setUser(user);
        tokenBean.setTokenHash(tokenHash);
        tokenBean.setVerificationType("REGISTER_VERIFY");
        tokenBean.setExpiresAt(tokenExpireAt);
        tokenBean.setUsedAt(null);
        emailVerificationTokenRepository.save(tokenBean);

        mailService.sendEmailVerified(user.getEmail(), token);
    }

    @Transactional
    public void changeEmailConfirm(String emailConfirmToken, HttpServletRequest httpRequest) {
        String token = jwtService.hashSecureToken(emailConfirmToken);

        EmailVerificationTokenBean tokenBean = emailVerificationTokenRepository.findByTokenHashAndVerificationType(token, "CHANGE_EMAIL")
                .orElseThrow(() -> new BusinessException("EMAIL_CHANGE_TOKEN_INVALID", "驗證連結無效"));

        if (tokenBean.getUsedAt() != null) {
            throw new BusinessException("EMAIL_CHANGE_TOKEN_USED", "驗證連結已使用");
        }
        if (tokenBean.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new BusinessException("EMAIL_CHANGE_TOKEN_EXPIRED", "驗證連結已過期");
        }

        UserBean user = tokenBean.getUser();
        if (user.getPendingEmail() == null || user.getPendingEmail().isBlank()) {
            throw new BusinessException("EMAIL_CHANGE_NOT_FOUND", "沒有待驗證的新信箱");
        }
        if (usersRepository.existsByEmail(user.getPendingEmail())) {
            throw new BusinessException("EMAIL_ALREADY_USED", "此電子信箱已被使用");
        }
        LocalDateTime now = LocalDateTime.now();
        String oldEmail = user.getEmail();
        String newEmail = user.getPendingEmail();

        user.setEmail(user.getPendingEmail());
        user.setPendingEmail(null);
        user.setEmailVerified(true);
        user.setUpdatedDate(now);
        user.setTokenVersion(user.getTokenVersion() + 1);
        usersRepository.save(user);

        tokenBean.setUsedAt(now);
        emailVerificationTokenRepository.save(tokenBean);

        userSessionService.revokeSession(user.getId(), "EMAIL_CHANGE");

        auditLogService.createAuditLog(
                user.getId(),
                "EMAIL_CHANGE_CONFIRMED",
                "MEMBER",
                "USER",
                user.getId(),
                "{\"oldEmail\":\"" + oldEmail + "\",\"newEmail\":\"" + newEmail + "\"}",
                httpRequest
        );
    }

    @Transactional
    public UserMeResponse emailIsVerified(String emailVerifiedToken) {
        String tokenHash = jwtService.hashSecureToken(emailVerifiedToken);

        EmailVerificationTokenBean tokenBean = emailVerificationTokenRepository.findByTokenHashAndVerificationType(tokenHash, "REGISTER_VERIFY")
                .orElseThrow(() -> new BusinessException("EMAIL_VERIFICATION_TOKEN_NOT_FOUND", "找不到 Email 驗證資料"));

        if (tokenBean.getUsedAt() != null) {
            throw new BusinessException("EMAIL_VERIFICATION_TOKEN_USED", "Email 驗證連結已被使用");
        }

        if (tokenBean.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new BusinessException("EMAIL_VERIFICATION_TOKEN_EXPIRED", "Email 驗證連結已過期");
        }

        UserBean user = tokenBean.getUser();
        if (user.isEmailVerified()) {
            throw new BusinessException("EMAIL_ALREADY_VERIFIED", "Email 已經完成驗證");
        }

        UserProfilesBean profile = userProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new BusinessException("USERPROFILE_NOT_FOUND", "找不到使用者檔案"));

        List<String> roles = userRoleRepository.findByUser_Id(user.getId())
                .stream().map(userRoleBean -> userRoleBean.getRole().getRoleCode()).toList();

        LocalDateTime now = LocalDateTime.now();

        tokenBean.setUsedAt(now);
        emailVerificationTokenRepository.save(tokenBean);

        user.setEmailVerified(true);
        user.setUpdatedDate(now);
        UserBean save = usersRepository.save(user);

        return new UserMeResponse(
                save.getId(),
                save.getEmail(),
                profile.getAvatarUrl(),
                profile.getNickname(),
                save.getStatus(),
                save.isEmailVerified(),
                save.isTwoFactorEnabled(),
                roles
        );
    }

    @Transactional
    public LoginResponse issueLoginToken(UserBean user, HttpServletRequest HttpRequest) {

        UserProfilesBean profiles = userProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new BusinessException("PROFILE_NOT_FOUND", "找不到會員資料"));

        List<String> roles = userRoleRepository.findByUser_Id(user.getId())
                .stream().map(userRoleBean -> userRoleBean.getRole().getRoleCode()).toList();

        String accessToken = jwtService.getAccessToken(user);
        LocalDateTime accessTokenExpireAt = jwtService.getAccessTokenExpireAt();
        String refreshToken = jwtService.generateSecureToken();
        String hashedRefreshToken = jwtService.hashSecureToken(refreshToken);
        LocalDateTime refreshTokenExpireAt = jwtService.getSecureTokenExpireAt();
        String ipAddress = getClientIp(HttpRequest);
        String userAgent = HttpRequest.getHeader("User-Agent");

        userSessionService.revokeSession(user.getId(), "NEW_LOGIN");
        userSessionService.createSession(user, hashedRefreshToken, ipAddress, userAgent, refreshTokenExpireAt);

        userNotificationService.emailVerifyNotification(user);
        userNotificationService.userProfileNotification(user);

        return LoginResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .nickName(profiles.getNickname())
                .avatarUrl(profiles.getAvatarUrl())
                .roles(roles)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .accessTokenExpiresIn(accessTokenExpireAt)
                .refreshTokenExpiresIn(refreshTokenExpireAt)
                .requiresTwoFactor(false)
                .loginChallengeToken(null)
                .build();
    }

    private void passwordFailedAttempt(UserBean user, HttpServletRequest HttpRequest) {
        int count = user.getFailedLoginCount() + 1;
        LocalDateTime now = LocalDateTime.now();

        user.setFailedLoginCount(count);
        user.setUpdatedDate(now);

        if (count >= 3) {
            user.setStatus("LOCKED");
            user.setStatusUntil(now.plusMinutes(10));
            loginAttemptService.createFailureRecord(user.getEmail(), HttpRequest, "ACCOUNT_LOCKED_BY_PASSWORD_FAILED");

        } else loginAttemptService.createFailureRecord(user.getEmail(), HttpRequest, "INVALID_PASSWORD");

        usersRepository.save(user);
    }

    public String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip != null && ip.isBlank()) {
            return ip.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
