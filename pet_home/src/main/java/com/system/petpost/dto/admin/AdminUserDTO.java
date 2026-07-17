package com.system.petpost.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 中文註解：論壇後台會員管理列表 DTO。
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdminUserDTO {

    // 中文註解：會員 ID。
    private Long userId;

    // 中文註解：會員 Email。
    private String email;

    // 中文註解：會員顯示名稱，優先使用 nickname。
    private String userName;

    // 中文註解：會員角色，例如 USER / ADMIN。
    private String roleCode;

    // 中文註解：會員文章數。
    private Long postCount;

    // 中文註解：被檢舉次數。
    private Long reportCount;

    // 中文註解：會員狀態，例如 ACTIVE / SUSPENDED。
    private String status;
}