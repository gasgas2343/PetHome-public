package com.system.member.controller;

import com.system.member.dto.ApiResponse;
import com.system.member.dto.request.ChangeEmailRequest;
import com.system.member.dto.request.ChangePwdRequest;
import com.system.member.dto.request.PetDataRequest;
import com.system.member.dto.request.UserProfileRequest;
import com.system.member.dto.response.*;
import com.system.member.exception.BusinessException;
import com.system.member.security.annotation.RequireEmailVerified;
import com.system.member.security.annotation.RequirePermission;
import com.system.member.service.PetService;
import com.system.member.service.UserNotificationService;
import com.system.member.service.UserProfileService;
import com.system.member.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private UserProfileService UserProfileService;
    @Autowired
    private PetService petService;
    @Autowired
    private UserNotificationService userNotificationService;

    @GetMapping("/me")
    public ApiResponse<UserMeResponse> me(Authentication authentication) {
        if (authentication == null) {
            throw new BusinessException("AUTH_REQUIRED", "請先登入", HttpStatus.UNAUTHORIZED);
        }

        Long userid = Long.valueOf(authentication.getName());
        UserMeResponse response = userService.getMe(userid);
        return ApiResponse.success("USER_ME_SUCCESS", "取得目前使用者資料成功", response);
    }

    @GetMapping("/me/module")
    public ApiResponse<UserModuleResponse> getModule(Authentication authentication) {

        Long userid = Long.valueOf(authentication.getName());
        UserModuleResponse response = userService.getModule(userid);

        return ApiResponse.success("USER_MODULE_SUCCESS", "取得目前使用者權限模組成功", response);
    }

    @RequirePermission(value = "MEMBER_PROFILE_READ")
    @GetMapping("/me/profile")
    public ApiResponse<GetUserProfileResponse> getProfile(Authentication authentication) {
        Long userid = Long.valueOf(authentication.getName());
        GetUserProfileResponse response = UserProfileService.getUserProfile(userid);

        return ApiResponse.success("PROFILE_GET_SUCCESS", "取得會員資料成功", response);
    }

    @RequirePermission(value = "PET_READ")
    @GetMapping("/me/pets")
    public ApiResponse<List<PetDataResponse>> getPets(Authentication authentication) {
        Long userid = Long.valueOf(authentication.getName());
        List<PetDataResponse> response = petService.getPet(userid);

        return ApiResponse.success("PET_LIST_GET_SUCCESS", "取得寵物列表成功", response);

    }

    @RequirePermission(value = "PET_CREATE")
    @PostMapping("/me/pets")
    public ResponseEntity<ApiResponse<PetDataResponse>> createPet(Authentication authentication, @Valid @RequestBody PetDataRequest request) {
        Long userid = Long.valueOf(authentication.getName());
        PetDataResponse response = petService.createPet(userid, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("PET_CREATED", "寵物新增成功", response));
    }

    @RequireEmailVerified
    @RequirePermission(value = "MEMBER_PASSWORD_UPDATE")
    @PutMapping("/me/password")
    public ApiResponse<RefreshTokenResponse> changePwd(Authentication authentication, @Valid @RequestBody ChangePwdRequest request, HttpServletRequest httpRequest) {
        Long id = Long.valueOf(authentication.getName());
        RefreshTokenResponse response = userService.changePassword(id, request, httpRequest);

        return ApiResponse.success("PASSWORD_UPDATED", "密碼修改成功", response);
    }

    @RequirePermission(value = "MEMBER_EMAIL_CHANGE")
    @PostMapping("/me/email")
    public ApiResponse<Void> changeEmail(Authentication authentication, @Valid @RequestBody ChangeEmailRequest request, HttpServletRequest httpRequest) {
        Long userId = Long.valueOf(authentication.getName());
        userService.changeEmail(userId, request, httpRequest);

        return ApiResponse.success( "EMAIL_CHANGE_REQUEST_SUCCESS", "驗證信已寄到新的電子信箱", null);
    }

    @RequirePermission(value = "MEMBER_PROFILE_UPDATE")
    @PutMapping("/me/profile")
    public ApiResponse<UserProfileResponse> updateProfile(Authentication authentication, @Valid @RequestBody UserProfileRequest request) {
        Long userid = Long.valueOf(authentication.getName());
        UserProfileResponse response = UserProfileService.updateProfile(userid, request);

        return ApiResponse.success("PROFILE_UPDATED", "會員資料更新成功", response);
    }

    @RequirePermission(value = "PET_UPDATE")
    @PutMapping("/me/pets/{petId}")
    public ApiResponse<PetDataResponse> updatePet(Authentication authentication, @PathVariable Long petId, @Valid @RequestBody PetDataRequest request) {
        Long userId = Long.valueOf(authentication.getName());
        PetDataResponse response = petService.updatePet(userId, petId, request);

        return ApiResponse.success("PET_UPDATED", "寵物資料更新成功", response);
    }

    @RequirePermission(value = "PET_DELETE")
    @DeleteMapping("/me/pets/{petId}")
    public ApiResponse<DelPetResponse> deletePet(Authentication authentication, @PathVariable Long petId) {
        Long userId = Long.valueOf(authentication.getName());
        DelPetResponse response = petService.deletePet(userId, petId);

        return ApiResponse.success("PET_DELETED", "寵物刪除成功", response);
    }

    @RequirePermission(value = "MEMBER_NOTIFICATION_READ")
    @GetMapping("/me/notifications")
    public ApiResponse<List<NotificationResponse>> getAllNotifications(Authentication authentication) {
        Long userId = Long.valueOf(authentication.getName());
        List<NotificationResponse> response = userNotificationService.getAllNotifications(userId);

        return ApiResponse.success("NOTIFICATION_LIST_GET_SUCCESS", "查詢通知列表成功", response);
    }

    @RequirePermission(value = "MEMBER_NOTIFICATION_READ")
    @GetMapping("/me/notifications/unread-count")
    public ApiResponse<UnreadNotificationsResponse> unreadNotificationsCount(Authentication authentication) {
        Long userId = Long.valueOf(authentication.getName());
        UnreadNotificationsResponse response = userNotificationService.unreadNotificationsCount(userId);

        return ApiResponse.success("NOTIFICATION_UNREAD_COUNT_SUCCESS", "計算未讀通知成功", response);
    }

    @RequirePermission(value = "MEMBER_NOTIFICATION_UPDATE")
    @PutMapping("/me/notifications/{notificationId}/read")
    public ApiResponse<Void> isReadNotification(Authentication authentication, @PathVariable Long notificationId) {
        Long userId = Long.valueOf(authentication.getName());
        userNotificationService.isReadNotification(userId, notificationId);

        return ApiResponse.success("NOTIFICATION_UPDATE_SUCCESS", "通知已讀成功", null);
    }

    @RequirePermission(value = "MEMBER_NOTIFICATION_UPDATE")
    @PutMapping("/me/notifications/read-all")
    public ApiResponse<Void> isReadAllNotifications(Authentication authentication) {
        Long userId = Long.valueOf(authentication.getName());
        userNotificationService.isReadAllNotifications(userId);

        return ApiResponse.success("NOTIFICATION_LIST_UPDATE_SUCCESS", "所有通知已讀成功", null);
    }

}
