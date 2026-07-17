package com.system.petpost.controller.admin;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.system.member.dto.ApiResponse;
import com.system.member.security.annotation.RequirePermission;
import com.system.petpost.dto.admin.AdminUserDTO;
import com.system.petpost.service.AdminForumUserService;

import lombok.RequiredArgsConstructor;

/**
 * 中文註解：論壇後台會員管理。
 *
 * 注意：
 * 這裡只做「論壇使用權限」管理。
 * 不負責登入、註冊、密碼、Email 修改。
 */
@RestController
@RequestMapping("/admin/forum-users")
@RequiredArgsConstructor
public class AdminForumUserController {

    private final AdminForumUserService adminForumUserService;

    /**
     * 中文註解：查詢論壇會員列表。
     */
    @GetMapping
    @RequirePermission("ADMIN_USER_READ")
    public ApiResponse<List<AdminUserDTO>> findAll() {
        return ApiResponse.success(
                "ADMIN_FORUM_USER_LIST_SUCCESS",
                "取得論壇會員列表成功",
                adminForumUserService.findAll());
    }

    /**
     * 中文註解：論壇停權。
     */
    @PutMapping("/{userId}/suspend")
    @RequirePermission("ADMIN_USER_UPDATE")
    public ApiResponse<Void> suspendUser(@PathVariable Long userId) {
        adminForumUserService.suspendUser(userId);

        return ApiResponse.success(
                "ADMIN_FORUM_USER_SUSPENDED",
                "會員已停權",
                null);
    }

    /**
     * 中文註解：解除論壇停權。
     */
    @PutMapping("/{userId}/activate")
    @RequirePermission("ADMIN_USER_UPDATE")
    public ApiResponse<Void> activateUser(@PathVariable Long userId) {
        adminForumUserService.activateUser(userId);

        return ApiResponse.success(
                "ADMIN_FORUM_USER_ACTIVATED",
                "會員已解除停權",
                null);
    }
}