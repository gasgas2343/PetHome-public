package com.system.member.controller;

import com.system.member.dto.ApiResponse;
import com.system.member.dto.request.TotpCloseRequest;
import com.system.member.dto.request.TotpConfirmRequest;
import com.system.member.dto.response.TotpSetupResponse;
import com.system.member.dto.response.TotpStatusResponse;
import com.system.member.security.annotation.RequireEmailVerified;
import com.system.member.security.annotation.RequirePermission;
import com.system.member.service.AuthService;
import com.system.member.service.TwoFactorService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users/me/2fa")
public class TotpController {

    @Autowired
    TwoFactorService twoFactorService;
    @Autowired
    private AuthService authService;

    @RequireEmailVerified
    @RequirePermission(value = "MEMBER_TOTP_SETUP")
    @PostMapping("/setup")
    public ApiResponse<TotpSetupResponse> setupTotp(Authentication authentication){
        Long userId = Long.valueOf(authentication.getName());
        TotpSetupResponse response = twoFactorService.setupTotp(userId);

        return ApiResponse.success("TOTP_SETUP_SUCCESS", "請使用驗證器 App 掃描 QR Code", response);
    }

    @RequireEmailVerified
    @RequirePermission(value = "MEMBER_TOTP_CONFIRM")
    @PostMapping("/confirm")
    public ApiResponse<Void> totpConfirm(Authentication authentication, @RequestBody @Valid TotpConfirmRequest totpRequest, HttpServletRequest httpRequest){
        Long userId = Long.valueOf(authentication.getName());
        twoFactorService.totpConfirm(userId, totpRequest, httpRequest);

        return ApiResponse.success("TOTP_ENABLED", "兩步驟驗證已啟用", null);
    }

    @RequireEmailVerified
    @RequirePermission(value = "MEMBER_TOTP_DISABLE")
    @PostMapping("/disable")
    public ApiResponse<TotpStatusResponse> totpDisable(Authentication authentication, @RequestBody @Valid TotpCloseRequest totpRequest, HttpServletRequest httpRequest){
        Long userId = Long.valueOf(authentication.getName());
        TotpStatusResponse response = twoFactorService.totpClose(userId, totpRequest, httpRequest);

        return ApiResponse.success( "TOTP_DISABLE_SUCCESS", "兩步驟驗證已關閉", response);
    }
}
