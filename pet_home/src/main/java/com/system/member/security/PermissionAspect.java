package com.system.member.security;

import com.system.member.entity.UserBean;
import com.system.member.exception.BusinessException;
import com.system.member.repository.UsersRepository;
import com.system.member.security.annotation.RequirePermission;
import com.system.member.service.PermissionService;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class PermissionAspect {

    @Autowired
    private PermissionService permissionService;
    @Autowired
    private UsersRepository usersRepository;

    @Before("@annotation(requirePermission)")
    public void checkPermission(RequirePermission requirePermission) {
        System.out.println("進入權限檢查：" + requirePermission.value());

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null) {
            throw new BusinessException("AUTH_REQUIRED", "請先登入");
        }

        Long userId = Long.valueOf(authentication.getName());

        String permission = requirePermission.value();
        if (!permissionService.hasPermission(userId, permission)) {
            throw new BusinessException("PERMISSION_DENIED", "沒有操作權限");
        }
    }

    @Before("@annotation(com.system.member.security.annotation.RequireEmailVerified)")
    public void checkEmailVerified() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getName() == null) {
            throw new BusinessException("AUTH_REQUIRED", "請先登入");
        }

        Long userId = Long.valueOf(authentication.getName());

        UserBean user = usersRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("USER_NOT_FOUND", "找不到使用者"));

        if (!user.isEmailVerified()) {
            throw new BusinessException("EMAIL_NOT_VERIFIED", "請先完成信箱驗證", HttpStatus.FORBIDDEN);
        }
    }
}
