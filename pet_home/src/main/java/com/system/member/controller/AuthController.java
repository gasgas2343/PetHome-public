package com.system.member.controller;

import com.system.member.dto.ApiResponse;
import com.system.member.dto.request.*;
import com.system.member.dto.response.LoginResponse;
import com.system.member.dto.response.RefreshTokenResponse;
import com.system.member.dto.response.RegisterResponse;
import com.system.member.dto.response.UserMeResponse;
import com.system.member.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<RegisterResponse>> register(@Valid @RequestBody RegisterRequest registerRequest) {
        RegisterResponse response = authService.register(registerRequest);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("REGISTER_SUCCESS", "註冊成功", response));
    }

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest, HttpServletRequest request) {
        LoginResponse response = authService.login(loginRequest, request);
        return ApiResponse.success("LOGIN_SUCCESS", "登入成功", response);
    }

    @PostMapping("/login/totp")
    public ApiResponse<LoginResponse> totpLogin(@RequestBody @Valid TotpLoginRequest totpRequest, HttpServletRequest httpRequest){
        LoginResponse response = authService.totpLogin(totpRequest, httpRequest);

        return ApiResponse.success("LOGIN_SUCCESS", "登入成功", response);
    }

    @PostMapping("/forgot-password")
    public ApiResponse<Void> forgotPassword(@Valid @RequestBody ForgotPwdRequest request, HttpServletRequest httpRequest) {
        authService.sendPwdResetToken(request, httpRequest);

        return  ApiResponse.success("PASSWORD_RESET_REQUEST_ACCEPTED",
                "如果此 Email 已註冊，我們已寄出密碼重設信", null);
    }

    @PostMapping("/reset-password")
    public ApiResponse<Void> resetPassword(@Valid @RequestBody SetNewPwdRequest request, HttpServletRequest httpRequest) {
        authService.setNewPwd(request, httpRequest);

        return ApiResponse.success("PASSWORD_RESET_SUCCESS", "密碼重設成功，請重新登入", null);
    }

    @PostMapping("/email-change-confirm")
    public ApiResponse<Void> emailChangeConfirm(@RequestBody @Valid ChangeEmailConfirmRequest request, HttpServletRequest httpRequest){
        authService.changeEmailConfirm(request.getToken(), httpRequest);

        return ApiResponse.success( "EMAIL_CHANGE_CONFIRM_SUCCESS", "信箱更改成功，請重新登入", null);
    }

    @PostMapping("/verify-email")
    public ApiResponse<UserMeResponse> verifyEmail(@RequestParam String token){
        UserMeResponse response = authService.emailIsVerified(token);

        return ApiResponse.success("EMAIL_VERIFICATION_SUCCESS", "Email 驗證成功", response);
    }

    @PostMapping("/send-email-verification")
    public ApiResponse<Void> sendVerification(@Valid @RequestBody SendVerificationEmailRequest request) {
        authService.requestEmailVerified(request);

        return ApiResponse.success("EMAIL_VERIFICATION_SENT", "驗證信已寄出，請至信箱完成 Email 驗證", null);
    }

    @PostMapping("/refresh")
    public ApiResponse<RefreshTokenResponse> refreshToken(@RequestBody RefreshTokenRequest refreshTokenRequest, HttpServletRequest request) {
        RefreshTokenResponse response = authService.refreshToken(refreshTokenRequest, request);
        return ApiResponse.success("TOKEN_REFRESH_SUCCESS", "Token 更新成功", response);
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(@RequestBody RefreshTokenRequest request) {
        authService.logout(request);

        return ApiResponse.success("LOGOUT_SUCCESS", "登出成功", null);
    }
}
