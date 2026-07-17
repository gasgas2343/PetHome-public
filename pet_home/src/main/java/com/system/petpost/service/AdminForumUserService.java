package com.system.petpost.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.system.member.entity.UserBean;
import com.system.member.entity.UserProfilesBean;
import com.system.member.exception.BusinessException;
import com.system.member.repository.UserProfileRepository;
import com.system.member.repository.UserRoleRepository;
import com.system.member.repository.UsersRepository;
import com.system.petpost.constant.PostStatus;
import com.system.petpost.dto.admin.AdminUserDTO;
import com.system.petpost.repository.ForumPostRepository;
import com.system.petpost.repository.ReportRepository;

import lombok.RequiredArgsConstructor;

/**
 * 中文註解：論壇後台會員管理 Service。
 *
 * 這裡只改 users.status。
 * 若隊友登入系統之後也檢查 users.status，
 * 就可以變成全站停權。
 */
@Service
@RequiredArgsConstructor
public class AdminForumUserService {

    private final UsersRepository usersRepository;
    private final UserProfileRepository userProfileRepository;
    private final UserRoleRepository userRoleRepository;
    private final ForumPostRepository forumPostRepository;
    private final ReportRepository reportRepository;

    /**
     * 中文註解：查詢論壇會員列表。
     */
    @Transactional(readOnly = true)
    public List<AdminUserDTO> findAll() {
        return usersRepository.findAll()
                .stream()
                .map(this::toAdminUserDTO)
                .toList();
    }

    /**
     * 中文註解：停權會員。
     */
    @Transactional
    public void suspendUser(Long userId) {
        UserBean user = findUser(userId);

        if ("SUSPENDED".equals(user.getStatus())) {
            throw new BusinessException(
                    "USER_ALREADY_SUSPENDED",
                    "會員已經是停權狀態");
        }

        user.setStatus("SUSPENDED");
        user.setUpdatedDate(LocalDateTime.now());

        usersRepository.save(user);
    }

    /**
     * 中文註解：解除停權。
     */
    @Transactional
    public void activateUser(Long userId) {
        UserBean user = findUser(userId);

        user.setStatus("ACTIVE");
        user.setStatusUntil(null);
        user.setUpdatedDate(LocalDateTime.now());

        usersRepository.save(user);
    }

    private UserBean findUser(Long userId) {
        if (userId == null) {
            throw new BusinessException(
                    "USER_ID_REQUIRED",
                    "會員ID不可為空");
        }

        return usersRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(
                        "USER_NOT_FOUND",
                        "找不到會員"));
    }

    private AdminUserDTO toAdminUserDTO(UserBean user) {
        UserProfilesBean profile = userProfileRepository
                .findByUserId(user.getId())
                .orElse(null);

        String roleCode = userRoleRepository
                .findByUser_Id(user.getId())
                .stream()
                .map(userRole -> userRole.getRole().getRoleCode())
                .collect(Collectors.joining(", "));

        Long postCount = forumPostRepository
                .countByUserIdAndStatus(
                        user.getId(),
                        PostStatus.ACTIVE);

        Long reportCount = reportRepository
                .countByReportedUserId(user.getId());

        return new AdminUserDTO(
                user.getId(),
                user.getEmail(),
                profile != null ? profile.getNickname() : null,
                roleCode,
                postCount,
                reportCount,
                user.getStatus());
    }
}