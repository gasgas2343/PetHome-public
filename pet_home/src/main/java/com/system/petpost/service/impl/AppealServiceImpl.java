package com.system.petpost.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.system.member.entity.UserBean;
import com.system.member.exception.BusinessException;

import com.system.petpost.constant.AppealStatus;
import com.system.petpost.constant.NotificationType;
import com.system.petpost.constant.PostStatus;
import com.system.petpost.constant.CommentStatus;

import com.system.petpost.dto.AppealCreateDTO;
import com.system.petpost.dto.admin.AdminAppealDTO;
import com.system.petpost.entity.Appeal;
import com.system.petpost.entity.ForumPost;
import com.system.petpost.entity.ForumComment;

import com.system.petpost.service.AppealService;
import com.system.petpost.service.NotificationService;

import com.system.petpost.repository.AppealRepository;
import com.system.petpost.repository.ForumPostRepository;
import com.system.petpost.repository.CommentRepository;

import lombok.RequiredArgsConstructor;

import com.system.member.repository.UsersRepository;

import com.system.member.dto.request.CreateNotificationRequest;
import com.system.member.service.UserNotificationService;

import com.system.member.repository.UserNotificationRepository;

/**
 * 中文註解：
 * reporterId 由 Controller 從 JWT 取得後塞入 DTO，
 * 前端不再傳 reporterId。
 */
@Service
@RequiredArgsConstructor
public class AppealServiceImpl implements AppealService {
    private final UserNotificationRepository userNotificationRepository;

    private final UserNotificationService userNotificationService;

    private final UsersRepository usersRepository;

    private final CommentRepository commentRepository;

    // 中文註解：通知 Service，申訴審核後用來建立通知。
    private final NotificationService notificationService;

    // 中文註解：申訴 Repository，負責 forum_appeals 資料表。
    private final AppealRepository appealRepository;

    // 中文註解：文章 Repository，用來檢查被申訴文章是否存在、是否屬於本人、是否已被隱藏。
    private final ForumPostRepository postRepository;

    /**
     * 中文註解：建立申訴。
     */
    @Override
    public Appeal create(AppealCreateDTO dto) {
        validateCreateAppeal(dto);
        // 中文註解：文章申訴
        if ("POST".equalsIgnoreCase(dto.getTargetType())) {
            ForumPost post = postRepository.findById(dto.getTargetId())
                    .orElseThrow(() -> new BusinessException(
                            "POST_NOT_FOUND",
                            "文章不存在"));
            if (!post.getUserId().equals(dto.getUserId())) {
                throw new BusinessException(
                        "POST_OWNER_REQUIRED",
                        "只能申訴自己的文章");
            }
            if (post.getStatus() == PostStatus.DELETED) {
                throw new BusinessException(
                        "POST_DELETED_NOT_APPEALABLE",
                        "文章已刪除，無法申訴");
            }
            if (post.getStatus() != PostStatus.BLOCKED) {
                throw new BusinessException(
                        "POST_NOT_BLOCKED",
                        "只有被隱藏的文章可以申訴");
            }
            boolean existsPendingAppeal = appealRepository.existsByUserIdAndTargetTypeAndTargetIdAndStatus(
                    dto.getUserId(),
                    "POST",
                    post.getPostId(),
                    AppealStatus.PENDING);
            if (existsPendingAppeal) {
                throw new BusinessException(
                        "APPEAL_DUPLICATED",
                        "此文章已有待審核申訴");
            }
            Appeal appeal = new Appeal();
            appeal.setUserId(dto.getUserId());
            appeal.setTargetType("POST");
            appeal.setTargetId(post.getPostId());
            appeal.setAppealType("POST");
            appeal.setSubject(dto.getSubject());
            appeal.setReason(dto.getReason());
            appeal.setImageUrl(dto.getImageUrl());
            appeal.setStatus(AppealStatus.PENDING);
            appeal.setCreatedAt(LocalDateTime.now());

            return appealRepository.save(appeal);
        }
        // 中文註解：留言申訴
        if ("COMMENT".equalsIgnoreCase(dto.getTargetType())) {
            ForumComment comment = commentRepository.findById(dto.getTargetId())
                    .orElseThrow(() -> new BusinessException(
                            "COMMENT_NOT_FOUND",
                            "留言不存在"));
            if (!comment.getUserId().equals(dto.getUserId())) {
                throw new BusinessException(
                        "COMMENT_OWNER_REQUIRED",
                        "只能申訴自己的留言");
            }
            if (comment.getStatus() == CommentStatus.DELETED) {
                throw new BusinessException(
                        "COMMENT_DELETED_NOT_APPEALABLE",
                        "留言已刪除，無法申訴");
            }
            if (comment.getStatus() != CommentStatus.BLOCKED) {
                throw new BusinessException(
                        "COMMENT_NOT_BLOCKED",
                        "只有被隱藏的留言可以申訴");
            }
            boolean existsPendingAppeal = appealRepository.existsByUserIdAndTargetTypeAndTargetIdAndStatus(
                    dto.getUserId(),
                    "COMMENT",
                    comment.getCommentId(),
                    AppealStatus.PENDING);
            if (existsPendingAppeal) {
                throw new BusinessException(
                        "APPEAL_DUPLICATED",
                        "此留言已有待審核申訴");
            }
            Appeal appeal = new Appeal();
            appeal.setUserId(dto.getUserId());
            appeal.setTargetType("COMMENT");
            appeal.setTargetId(comment.getCommentId());
            appeal.setAppealType("COMMENT");
            appeal.setSubject(dto.getSubject());
            appeal.setReason(dto.getReason());
            appeal.setImageUrl(dto.getImageUrl());
            appeal.setStatus(AppealStatus.PENDING);
            appeal.setCreatedAt(LocalDateTime.now());

            return appealRepository.save(appeal);
        }
        // 中文註解：舊流程，針對檢舉案件申訴
        if (dto.getReportId() == null) {
            throw new BusinessException(
                    "REPORT_REQUIRED",
                    "申訴必須對應檢舉案件、文章或留言");
        }
        if (appealRepository.existsByReportId(dto.getReportId())) {
            throw new BusinessException(
                    "APPEAL_DUPLICATED",
                    "此檢舉案件已提出過申訴");
        }
        Appeal appeal = new Appeal();
        appeal.setReportId(dto.getReportId());
        appeal.setUserId(dto.getUserId());
        appeal.setAppealType(dto.getAppealType());
        appeal.setSubject(dto.getSubject());
        appeal.setReason(dto.getReason());
        appeal.setImageUrl(dto.getImageUrl());
        appeal.setStatus(AppealStatus.PENDING);
        appeal.setCreatedAt(LocalDateTime.now());

        return appealRepository.save(appeal);
    }

    /**
     * 中文註解：查詢所有申訴。
     */
    @Override
    public List<Appeal> findAll() {
        return appealRepository.findAll();
    }

    /**
     * 中文註解：查詢單筆申訴。
     */
    @Override
    public Appeal findById(Long appealId) {

        if (appealId == null) {
            throw new BusinessException(
                    "APPEAL_ID_REQUIRED",
                    "申訴ID不可為空");
        }

        return appealRepository.findById(appealId)
                .orElseThrow(() -> new BusinessException(
                        "APPEAL_NOT_FOUND",
                        "申訴不存在"));
    }

    /**
     * 中文註解：查詢待審核申訴。
     */
    @Override
    public List<Appeal> findPendingAppeals() {
        return appealRepository.findByStatus(AppealStatus.PENDING);
    }

    /**
     * 中文註解：申訴通過。
     */
    @Override
    public Appeal approve(Long appealId, Long adminId, String note) {

        Appeal appeal = findById(appealId);

        validatePending(appeal);

        appeal.setStatus(AppealStatus.APPROVED);
        appeal.setReviewedBy(adminId);
        appeal.setReviewedAt(LocalDateTime.now());
        appeal.setAdminNote(note);

        Appeal savedAppeal = appealRepository.save(appeal);

        // 中文註解：通知申訴人申訴通過。
        notificationService.createNotification(
                appeal.getUserId(),
                NotificationType.APPEAL_RESULT,
                appeal.getAppealId(),
                "您的申訴已審核通過");

        userNotificationService.createNotification(
                CreateNotificationRequest.of(
                        appeal.getUserId(),
                        "FORUM",
                        "APPEAL_RESULT",
                        "申訴審核結果",
                        "您的申訴已審核通過。",
                        "APPEAL",
                        appeal.getAppealId()));

        return savedAppeal;
    }

    /**
     * 中文註解：申訴駁回。
     */
    @Override
    public Appeal reject(Long appealId, Long adminId, String note) {

        Appeal appeal = findById(appealId);

        validatePending(appeal);

        appeal.setStatus(AppealStatus.REJECTED);
        appeal.setReviewedBy(adminId);
        appeal.setReviewedAt(LocalDateTime.now());
        appeal.setAdminNote(note);

        Appeal savedAppeal = appealRepository.save(appeal);

        // 中文註解：通知申訴人申訴駁回。
        notificationService.createNotification(
                appeal.getUserId(),
                NotificationType.APPEAL_RESULT,
                appeal.getAppealId(),
                "您的申訴未通過");

        boolean exists = userNotificationRepository
                .existsByUser_IdAndNotificationTypeAndIsReadFalse(
                        appeal.getUserId(),
                        "APPEAL_RESULT");

        if (!exists) {

            userNotificationService.createNotification(
                    CreateNotificationRequest.of(
                            appeal.getUserId(),
                            "FORUM",
                            "APPEAL_RESULT",
                            "申訴審核結果",
                            "您的申訴已審核完成。",
                            "APPEAL",
                            appeal.getAppealId()));
        }

        return savedAppeal;
    }

    /**
     * 中文註解：建立申訴資料前的欄位驗證。
     */
    private void validateCreateAppeal(AppealCreateDTO dto) {

        if (dto == null) {
            throw new BusinessException(
                    "APPEAL_BODY_REQUIRED",
                    "申訴資料不可為空");
        }

        // 中文註解：申訴必須至少對應「檢舉案件」或「目標文章」其中一種。
        if (dto.getReportId() == null && dto.getTargetId() == null) {
            throw new BusinessException(
                    "APPEAL_TARGET_REQUIRED",
                    "申訴必須對應檢舉案件、文章或留言");
        }

        // 中文註解：如果是文章申訴，targetType 必須是 POST。
        if (dto.getTargetId() != null) {
            if (dto.getTargetType() == null || dto.getTargetType().trim().isEmpty()) {
                throw new BusinessException(
                        "TARGET_TYPE_REQUIRED",
                        "申訴目標類型不可為空");
            }
        }

        if (dto.getUserId() == null) {
            throw new BusinessException(
                    "USER_REQUIRED",
                    "申訴會員不可為空");
        }

        if (dto.getAppealType() == null || dto.getAppealType().trim().isEmpty()) {
            throw new BusinessException(
                    "APPEAL_TYPE_REQUIRED",
                    "申訴類型不可為空");
        }

        if (dto.getSubject() == null || dto.getSubject().trim().isEmpty()) {
            throw new BusinessException(
                    "APPEAL_SUBJECT_REQUIRED",
                    "請輸入申訴主旨");
        }

        if (dto.getReason() == null || dto.getReason().trim().isEmpty()) {
            throw new BusinessException(
                    "APPEAL_REASON_REQUIRED",
                    "請輸入申訴原因");
        }
    }

    /**
     * 中文註解：檢查申訴是否仍為待審核狀態。
     */
    private void validatePending(Appeal appeal) {

        if (appeal.getStatus() != AppealStatus.PENDING) {
            throw new BusinessException(
                    "APPEAL_ALREADY_REVIEWED",
                    "申訴已審核");
        }
    }

    @Override
    public List<AdminAppealDTO> findPendingAppealDTOs() {
        return appealRepository.findByStatus(AppealStatus.PENDING)
                .stream()
                .map(this::toAdminAppealDTO)
                .toList();
    }

    private AdminAppealDTO toAdminAppealDTO(Appeal appeal) {
        UserBean user = usersRepository.findById(appeal.getUserId()).orElse(null);

        AdminAppealDTO dto = new AdminAppealDTO();

        dto.setAppealId(appeal.getAppealId());
        dto.setUserId(appeal.getUserId());
        dto.setUserName(resolveUserName(user));
        dto.setUserEmail(user == null ? null : user.getEmail());
        dto.setTargetType(appeal.getTargetType());
        dto.setTargetId(appeal.getTargetId());
        dto.setAppealType(appeal.getAppealType());
        dto.setSubject(appeal.getSubject());
        dto.setReason(appeal.getReason());
        dto.setStatus(appeal.getStatus());

        return dto;
    }

    private String resolveUserName(UserBean user) {
        if (user == null) {
            return "未知會員";
        }

        if (user.getProfiles() != null
                && user.getProfiles().getNickname() != null
                && !user.getProfiles().getNickname().isBlank()) {
            return user.getProfiles().getNickname();
        }

        if (user.getProfiles() != null
                && user.getProfiles().getFullName() != null
                && !user.getProfiles().getFullName().isBlank()) {
            return user.getProfiles().getFullName();
        }

        return "會員 " + user.getId();
    }
}
