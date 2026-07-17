package com.system.petpost.controller.admin;

import com.system.member.repository.UsersRepository;
import com.system.member.security.annotation.RequirePermission;
import com.system.petpost.dto.admin.AdminDashboardSummaryDTO;
import com.system.petpost.repository.AppealRepository;
import com.system.petpost.repository.ForumPostRepository;
import com.system.petpost.repository.ReportRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 中文註解：管理後台首頁統計 API。
 */
@RestController
@RequestMapping("/admin/dashboard")
@RequiredArgsConstructor
public class AdminDashboardController {

    private final ForumPostRepository forumPostRepository;
    private final ReportRepository reportRepository;
    private final AppealRepository appealRepository;
    private final UsersRepository usersRepository;

    /**
     * 中文註解：取得管理首頁統計資料。
     *
     * 前端路徑：
     * GET /api/admin/dashboard/summary
     */
    @GetMapping("/summary")
    @RequirePermission("FORUM_DASHBOARD_VIEW")
    public AdminDashboardSummaryDTO getSummary() {
        Byte activeStatus = Byte.valueOf((byte) 1);
        Byte pendingStatus = Byte.valueOf((byte) 1);

        long postCount = forumPostRepository.countByStatus(activeStatus);
        long userCount = usersRepository.count();
        long pendingReportCount = reportRepository.countByStatus(pendingStatus);
        long pendingAppealCount = appealRepository.countByStatus(pendingStatus);

        return new AdminDashboardSummaryDTO(
                postCount,
                userCount,
                pendingReportCount,
                pendingAppealCount
        );
    }
}