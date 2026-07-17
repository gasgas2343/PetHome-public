package com.system.petpost.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 中文註解：管理首頁統計資料 DTO。
 * 用來回傳後台首頁的四個統計卡片。
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdminDashboardSummaryDTO {

    // 中文註解：總文章數。
    private long postCount;

    // 中文註解：總會員數。
    private long userCount;

    // 中文註解：待處理檢舉數。
    private long pendingReportCount;

    // 中文註解：待處理申訴數。
    private long pendingAppealCount;
}