package com.system.member.service;

import com.system.member.dto.request.TotpCloseRequest;
import com.system.member.dto.request.TotpConfirmRequest;
import com.system.member.dto.response.TotpSetupResponse;
import com.system.member.dto.response.TotpStatusResponse;
import com.system.member.entity.UserBean;
import com.system.member.entity.UserTwoFactorSettingBean;
import com.system.member.exception.BusinessException;
import com.system.member.repository.UserTwoFactorSettingRepository;
import com.system.member.repository.UsersRepository;
import com.system.member.util.CryptoUtil;
import com.system.member.util.TotpUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class TwoFactorService {
    @Autowired
    private UserTwoFactorSettingRepository userTwoFactorSettingRepository;
    @Autowired
    private UsersRepository usersRepository;
    @Autowired
    private CryptoUtil cryptoUtil;
    @Autowired
    private AuditLogService auditLogService;
    @Autowired
    private JwtService jwtService;

    @Transactional
    public TotpSetupResponse setupTotp(Long userId){
        UserBean user = usersRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("USER_NOT_FOUND", "找不到使用者"));

        if(!user.isEmailVerified()){
            throw new BusinessException("EMAIL_NOT_VERIFIED", "請先驗證信箱");
        }

        if(user.isTwoFactorEnabled()){
            throw new BusinessException("TOTP_ALREADY_ENABLED", "已啟用兩部驗證");
        }

        Optional<UserTwoFactorSettingBean> OpSetting =
                userTwoFactorSettingRepository.findByUser_IdAndMethod(userId, "TOTP");

        if (OpSetting.isPresent() && Boolean.TRUE.equals(OpSetting.get().getEnabled())) {
            throw new BusinessException("TOTP_ALREADY_ENABLED", "已啟用兩部驗證");
        }

        UserTwoFactorSettingBean setting = userTwoFactorSettingRepository.findByUser_IdAndMethod(userId, "TOTP")
                .orElseGet(UserTwoFactorSettingBean::new);

        String secret = TotpUtil.generateTotpSecret();
        String encrypt = cryptoUtil.encrypt(secret);
        setting.setUser(user);
        setting.setMethod("TOTP");
        setting.setSecretEncrypted(encrypt);
        setting.setEnabled(false);
        setting.setVerifiedAt(null);
        setting.setLastUsedAt(null);
        userTwoFactorSettingRepository.save(setting);

        String qrCode;
        try {
            qrCode = TotpUtil.buildQrCodeUrl(user.getEmail(), secret);
        }catch (RuntimeException  e){
            throw new BusinessException("TOTP_QR_GENERATE_FAILED", "產生 QR Code 失敗");
        }
        return new TotpSetupResponse(qrCode, secret);
    }

    @Transactional
    public void totpConfirm(Long userId, TotpConfirmRequest request, HttpServletRequest httpRequest){
        UserBean user = usersRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("USER_NOT_FOUND", "找不到使用者"));

        if (user.isTwoFactorEnabled()) {
            throw new BusinessException("TOTP_ALREADY_ENABLED", "兩步驟驗證已啟用");
        }

        UserTwoFactorSettingBean setting = userTwoFactorSettingRepository.findByUser_IdAndMethod(userId, "TOTP")
                .orElseThrow(() -> new BusinessException("TOTP_NOT_SETUP", "尚未設定兩步驟驗證"));
        if (Boolean.TRUE.equals(setting.getEnabled())) {
            throw new BusinessException("TOTP_ALREADY_ENABLED", "兩步驟驗證已啟用");
        }

        String secret;
        try {
            secret = cryptoUtil.decrypt(setting.getSecretEncrypted());
        } catch (RuntimeException e) {
            throw new BusinessException("TOTP_SECRET_DECRYPT_FAILED", "兩步驟驗證設定異常");
        }

        if(!TotpUtil.verifyTotp(secret, request.getCode())){
            throw new BusinessException("TOTP_CODE_INVALID", "驗證碼錯誤或已過期");
        }
        LocalDateTime now = LocalDateTime.now();

        setting.setEnabled(true);
        setting.setVerifiedAt(now);
        setting.setLastUsedAt(now);
        setting.setUpdatedDate(now);
        user.setTwoFactorEnabled(true);
        user.setUpdatedDate(now);
        usersRepository.save(user);
        userTwoFactorSettingRepository.save(setting);

        auditLogService.createAuditLog(
                userId,
                "ENABLE_TOTP",
                "MEMBER",
                "USER",
                userId,
                "{\"enabled\":true}",
                httpRequest
        );
    }

    @Transactional
    public TotpStatusResponse totpClose(Long userId, TotpCloseRequest request, HttpServletRequest httpRequest){
        UserBean user = usersRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("USER_NOT_FOUND", "找不到使用者"));

        UserTwoFactorSettingBean setting = userTwoFactorSettingRepository.findByUser_IdAndMethod(userId, "TOTP")
                .orElseThrow(() -> new BusinessException("TOTP_NOT_SETUP", "尚未設定兩步驟驗證"));

        if (!user.isTwoFactorEnabled() || !Boolean.TRUE.equals(setting.getEnabled())) {
            throw new BusinessException("TOTP_NOT_ENABLED", "兩步驟驗證尚未啟用");
        }

        String secret = cryptoUtil.decrypt(setting.getSecretEncrypted());
        if(!TotpUtil.verifyTotp(secret, request.getCode())){
            throw new BusinessException("TOTP_CODE_INVALID", "驗證碼錯誤");
        }

        LocalDateTime now = LocalDateTime.now();
        user.setTwoFactorEnabled(false);
        user.setUpdatedDate(now);
        usersRepository.save(user);
        setting.setEnabled(false);
        setting.setUpdatedDate(now);
        userTwoFactorSettingRepository.save(setting);

        auditLogService.createAuditLog(
                userId,
                "TOTP_DISABLED",
                "MEMBER",
                "USER",
                userId,
                "{\"twoFactorEnabledBefore\":true,\"twoFactorEnabledAfter\":false}",
                httpRequest
        );
        return new TotpStatusResponse(false);
    }
}
