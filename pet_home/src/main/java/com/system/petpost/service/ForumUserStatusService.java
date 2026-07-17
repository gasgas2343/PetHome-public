package com.system.petpost.service;

import org.springframework.stereotype.Service;

import com.system.member.entity.UserBean;
import com.system.member.exception.BusinessException;
import com.system.member.repository.UsersRepository;

import lombok.RequiredArgsConstructor;

/**
 * 中文註解：論壇會員狀態檢查。
 *
 * 用途：
 * 發文、留言、按讚、收藏、檢舉前先檢查會員是否被停權。
 */
@Service
@RequiredArgsConstructor
public class ForumUserStatusService {

    private final UsersRepository usersRepository;

    /**
     * 中文註解：檢查會員是否可使用論壇互動功能。
     */
    public void checkForumUsable(Long userId) {
        if (userId == null) {
            throw new BusinessException(
                    "USER_ID_REQUIRED",
                    "會員ID不可為空");
        }

        UserBean user = usersRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(
                        "USER_NOT_FOUND",
                        "找不到會員"));

        if ("SUSPENDED".equals(user.getStatus())) {
            throw new BusinessException(
                    "FORUM_USER_SUSPENDED",
                    "您的帳號已被停權，暫時無法使用論壇功能");
        }

        if ("BANNED".equals(user.getStatus())) {
            throw new BusinessException(
                    "FORUM_USER_BANNED",
                    "您的帳號已被封鎖，無法使用論壇功能");
        }

        if (!"ACTIVE".equals(user.getStatus())) {
            throw new BusinessException(
                    "FORUM_USER_NOT_ACTIVE",
                    "您的帳號狀態不可使用論壇功能");
        }
    }
}