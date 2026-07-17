package com.system.petpost.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.system.petpost.entity.ForumPost;
import com.system.petpost.entity.ForumComment;
import com.system.petpost.repository.ForumPostRepository;
import com.system.petpost.repository.CommentRepository;

import com.system.petpost.constant.NotificationType;
import com.system.petpost.constant.ReportStatus;
import com.system.petpost.dto.ReportCreateDTO;

import com.system.petpost.entity.Report;
import com.system.member.exception.BusinessException;
import com.system.petpost.repository.ReportRepository;

import com.system.petpost.service.ForumUserStatusService;
import com.system.petpost.service.NotificationService;
import com.system.petpost.service.ReportService;

import com.system.member.dto.request.CreateNotificationRequest;
import com.system.member.service.UserNotificationService;

import lombok.RequiredArgsConstructor;

/**
 * 檢舉 Service 實作
 *
 * 中文註解：
 * 1. 統一處理文章檢舉與留言檢舉。
 * 2. 防止檢舉自己。
 * 3. 防止同一會員重複檢舉同一篇文章或同一則留言。
 * 4. 管理員審核後建立通知。
 */
@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {
    private final UserNotificationService userNotificationService;

    private final ForumUserStatusService forumUserStatusService;

    // 中文註解：文章 Repository，檢舉文章成功後用來暫時隱藏文章。
    private final ForumPostRepository forumPostRepository;

    // 中文註解：留言 Repository，檢舉留言成功後用來暫時隱藏留言。
    private final CommentRepository commentRepository;

    // 中文註解：檢舉 Repository，負責 forum_reports 資料表。
    private final ReportRepository reportRepository;

    // 中文註解：通知 Service，檢舉審核後用來建立通知。
    private final NotificationService notificationService;

    /**
     * 中文註解：統一建立檢舉。
     *
     * 前端送 JSON：
     * {
     * "reporterId": 1,
     * "reportedUserId": 2,
     * "postId": 10,
     * "commentId": null,
     * "reason": "廣告內容"
     * }
     */
    @Override
    public Report createReport(ReportCreateDTO dto) {

        validateCreateReport(dto);
        // 中文註解：停權會員不可檢舉。
        forumUserStatusService.checkForumUsable(dto.getReporterId());

        boolean hasPost = dto.getPostId() != null;
        boolean hasComment = dto.getCommentId() != null;

        // 中文註解：防止同一人重複檢舉同一篇文章。
        if (hasPost && reportRepository.existsByReporterIdAndPostId(
                dto.getReporterId(),
                dto.getPostId())) {

            throw new BusinessException(
                    "REPORT_DUPLICATED",
                    "你已經檢舉過這篇文章");
        }

        // 中文註解：防止同一人重複檢舉同一則留言。
        if (hasComment && reportRepository.existsByReporterIdAndCommentId(
                dto.getReporterId(),
                dto.getCommentId())) {

            throw new BusinessException(
                    "REPORT_DUPLICATED",
                    "你已經檢舉過這則留言");
        }

        Report report = new Report();

        report.setReporterId(dto.getReporterId());
        report.setReportedUserId(dto.getReportedUserId());
        report.setPostId(dto.getPostId());
        report.setCommentId(dto.getCommentId());
        report.setReason(dto.getReason());
        report.setStatus(ReportStatus.PENDING);
        report.setCreatedAt(LocalDateTime.now());

        Report savedReport = reportRepository.save(report);

        // 中文註解：檢舉送出成功後，先把文章或留言暫時隱藏，等待管理員審核。
        hideReportedTarget(savedReport);

        return savedReport;
    }

    /**
     * 中文註解：查詢所有檢舉。
     */
    @Override
    public List<Report> findAll() {
        return reportRepository.findAll();
    }

    /**
     * 中文註解：查詢單筆檢舉。
     */
    @Override
    public Report findById(Long reportId) {

        if (reportId == null) {
            throw new BusinessException(
                    "REPORT_ID_REQUIRED",
                    "檢舉ID不可為空");
        }

        return reportRepository.findById(reportId)
                .orElseThrow(() -> new BusinessException(
                        "REPORT_NOT_FOUND",
                        "檢舉不存在"));
    }

    /**
     * 中文註解：查詢待審核檢舉。
     */
    @Override
    public List<Report> findPendingReports() {
        return reportRepository.findByStatus(ReportStatus.PENDING);
    }

    /**
     * 中文註解：檢舉成立。
     */
    @Override
    public Report approve(Long reportId, Long adminId, String note) {

        Report report = findById(reportId);

        validatePending(report);

        report.setStatus(ReportStatus.APPROVED);
        report.setReviewedBy(adminId);
        report.setReviewedAt(LocalDateTime.now());
        report.setAdminNote(note);

        Report savedReport = reportRepository.save(report);

        // 中文註解：通知被檢舉會員，該內容檢舉成立。
        notificationService.createNotification(
                report.getReportedUserId(),
                NotificationType.REPORT_RESULT,
                report.getReportId(),
                "您的內容已被檢舉成立");

        return savedReport;
    }

    /**
     * 中文註解：檢舉駁回。
     */
    @Override
    public Report reject(Long reportId, Long adminId, String note) {

        Report report = findById(reportId);

        validatePending(report);

        report.setStatus(ReportStatus.REJECTED);
        report.setReviewedBy(adminId);
        report.setReviewedAt(LocalDateTime.now());
        report.setAdminNote(note);

        Report savedReport = reportRepository.save(report);

        // 中文註解：通知被檢舉會員，該檢舉案件已結案。
        notificationService.createNotification(
                report.getReportedUserId(),
                NotificationType.REPORT_RESULT,
                report.getReportId(),
                "您的內容檢舉案件已結案");

        return savedReport;
    }

    /**
     * 中文註解：AI 自動檢舉文章。
     */
    @Override
    public Report createAiPostReport(Long reportedUserId, Long postId, String reason) {

        ReportCreateDTO dto = new ReportCreateDTO();

        // 中文註解：目前 1L 代表系統/管理員測試帳號。
        dto.setReporterId(1L);
        dto.setReportedUserId(reportedUserId);
        dto.setPostId(postId);
        dto.setReason(reason);

        return createReport(dto);
    }

    /**
     * 中文註解：檢舉成功後，將被檢舉的文章或留言改成隱藏狀態。
     * status：
     * 1 = 正常
     * 2 = 封鎖 / 隱藏
     * 3 = 刪除
     */
    private void hideReportedTarget(Report report) {

        if (report.getPostId() != null) {
            ForumPost post = forumPostRepository.findById(report.getPostId())
                    .orElseThrow(() -> new BusinessException(
                            "POST_NOT_FOUND",
                            "文章不存在"));

            post.setStatus((byte) 2);
            post.setBlockedReason("被使用者檢舉，等待管理員審核");
            post.setBlockedAt(LocalDateTime.now());
            post.setUpdatedAt(LocalDateTime.now());

            forumPostRepository.save(post);
        }

        if (report.getCommentId() != null) {
            ForumComment comment = commentRepository.findById(report.getCommentId())
                    .orElseThrow(() -> new BusinessException(
                            "COMMENT_NOT_FOUND",
                            "留言不存在"));

            comment.setStatus((byte) 2);
            comment.setBlockedReason("被使用者檢舉，等待管理員審核");
            comment.setBlockedAt(LocalDateTime.now());
            comment.setUpdatedAt(LocalDateTime.now());

            commentRepository.save(comment);
        }
    }

    /**
     * 中文註解：建立檢舉資料前的欄位驗證。
     */
    private void validateCreateReport(ReportCreateDTO dto) {

        if (dto == null) {
            throw new BusinessException(
                    "REPORT_BODY_REQUIRED",
                    "檢舉資料不可為空");
        }

        if (dto.getReporterId() == null) {
            throw new BusinessException(
                    "REPORTER_REQUIRED",
                    "檢舉人不可為空");
        }

        if (dto.getReportedUserId() == null) {
            throw new BusinessException(
                    "REPORTED_USER_REQUIRED",
                    "被檢舉人不可為空");
        }

        if (dto.getReporterId().equals(dto.getReportedUserId())) {
            throw new BusinessException(
                    "REPORT_SELF",
                    "不可檢舉自己");
        }

        if (dto.getReason() == null || dto.getReason().trim().isEmpty()) {
            throw new BusinessException(
                    "REPORT_REASON_REQUIRED",
                    "請輸入檢舉原因");
        }

        boolean hasPost = dto.getPostId() != null;
        boolean hasComment = dto.getCommentId() != null;

        if (!hasPost && !hasComment) {
            throw new BusinessException(
                    "REPORT_TARGET_REQUIRED",
                    "請選擇要檢舉的文章或留言");
        }

        if (hasPost && hasComment) {
            throw new BusinessException(
                    "REPORT_TARGET_INVALID",
                    "只能檢舉文章或留言其中一種");
        }
    }

    /**
     * 中文註解：檢查檢舉是否仍為待審核狀態。
     */
    private void validatePending(Report report) {

        if (report.getStatus() != ReportStatus.PENDING) {
            throw new BusinessException(
                    "REPORT_ALREADY_REVIEWED",
                    "檢舉已審核");
        }
    }
}
