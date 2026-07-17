package com.system.petpost.service;

import java.util.List;

import com.system.petpost.dto.ReportCreateDTO;
import com.system.petpost.entity.Report;

/**
 * 檢舉 Service 介面
 *
 * 中文註解：
 * Controller 只依賴這個介面，不直接依賴實作類別。
 * 真正邏輯放在 service.impl.ReportServiceImpl。
 */
public interface ReportService {

    /**
     * 中文註解：統一建立檢舉。
     * 文章檢舉：postId 有值、commentId 為 null。
     * 留言檢舉：commentId 有值、postId 為 null。
     */
    Report createReport(ReportCreateDTO dto);

    /**
     * 中文註解：查詢所有檢舉。
     */
    List<Report> findAll();

    /**
     * 中文註解：查詢單筆檢舉。
     */
    Report findById(Long reportId);

    /**
     * 中文註解：查詢待審核檢舉。
     */
    List<Report> findPendingReports();

    /**
     * 中文註解：管理員審核檢舉成立。
     */
    Report approve(Long reportId, Long adminId, String note);

    /**
     * 中文註解：管理員駁回檢舉。
     */
    Report reject(Long reportId, Long adminId, String note);

    /**
     * 中文註解：AI 自動建立文章檢舉。
     * 目前仍保留 reporterId=1 作為系統/管理員測試帳號。
     */
    Report createAiPostReport(Long reportedUserId, Long postId, String reason);
}
