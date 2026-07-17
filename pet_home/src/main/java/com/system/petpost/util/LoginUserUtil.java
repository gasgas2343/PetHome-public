package com.system.petpost.util;

import com.system.member.exception.BusinessException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * 中文註解：論壇模組取得目前登入者工具類。
 * 不修改 member 組員程式，只讀取 JWT Filter 放進 SecurityContext 的 userId。
 */
public class LoginUserUtil {

    public static Long getLoginUserId() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getName() == null) {
            throw new BusinessException("AUTH_REQUIRED", "請先登入");
        }

        return Long.valueOf(authentication.getName());
    }
}