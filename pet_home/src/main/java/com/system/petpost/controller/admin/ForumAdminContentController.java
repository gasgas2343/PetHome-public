package com.system.petpost.controller.admin;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.system.member.entity.UserBean;
import com.system.member.repository.UsersRepository;
import com.system.member.security.annotation.RequirePermission;

import com.system.petpost.dto.admin.AdminCommentDTO;
import com.system.petpost.dto.admin.AdminPostDTO;
import com.system.petpost.dto.admin.AdminReportDTO;

import com.system.petpost.entity.ForumComment;
import com.system.petpost.entity.ForumPost;
import com.system.petpost.entity.Report;

import com.system.petpost.repository.CommentRepository;
import com.system.petpost.repository.ForumPostRepository;
import com.system.petpost.repository.ReportRepository;

import com.system.petpost.constant.ReportStatus;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/admin/forum")
@RequiredArgsConstructor
public class ForumAdminContentController {

    private final ForumPostRepository forumPostRepository;
    private final CommentRepository commentRepository;
    private final ReportRepository reportRepository;
    private final UsersRepository usersRepository;

    /**
     * 中文註解：論壇後台查詢文章，包含作者名稱與帳號。
     */
    @GetMapping("/posts")
    @RequirePermission("FORUM_ADMIN_POST_MANAGE")
    public ResponseEntity<List<AdminPostDTO>> findAdminPosts() {
        List<AdminPostDTO> result = forumPostRepository.findAll()
                .stream()
                .filter(post -> post.getStatus() == null || post.getStatus() != 3)
                .map(this::toAdminPostDTO)
                .toList();

        return ResponseEntity.ok(result);
    }

    /**
     * 中文註解：論壇後台查詢留言，包含留言者名稱與帳號。
     * 後台刪除留言目前保留真刪除，所以這裡查詢現存資料即可。
     */
    @GetMapping("/comments")
    @RequirePermission("FORUM_ADMIN_POST_MANAGE")
    public ResponseEntity<List<AdminCommentDTO>> findAdminComments() {
        List<AdminCommentDTO> result = commentRepository.findAll()
                .stream()
                .map(this::toAdminCommentDTO)
                .toList();

        return ResponseEntity.ok(result);
    }

    /**
     * 中文註解：論壇後台查詢待審核檢舉，包含檢舉者與被檢舉者資料。
     */
    @GetMapping("/reports/pending")
    @RequirePermission("FORUM_REPORT_REVIEW")
    public ResponseEntity<List<AdminReportDTO>> findPendingReports() {
        List<AdminReportDTO> result = reportRepository.findByStatus((byte) 1)
                .stream()
                .map(this::toAdminReportDTO)
                .toList();

        return ResponseEntity.ok(result);
    }

    private AdminPostDTO toAdminPostDTO(ForumPost post) {
        UserBean user = findUser(post.getUserId());

        AdminPostDTO dto = new AdminPostDTO();

        dto.setPostId(post.getPostId());
        dto.setTitle(post.getTitle());
        dto.setUserId(post.getUserId());
        dto.setUserName(resolveUserName(user));
        dto.setUserEmail(resolveUserEmail(user));
        dto.setStatus(post.getStatus());
        dto.setViewCount(post.getViewCount());
        dto.setLikeCount(post.getLikeCount());
        
        boolean hasApprovedReport = reportRepository.existsByPostIdAndStatus(
                post.getPostId(),
                ReportStatus.APPROVED);
        dto.setCanDelete(
                post.getStatus() != null
                        && post.getStatus() == 2
                        && hasApprovedReport);
        return dto;
    }

    private AdminCommentDTO toAdminCommentDTO(ForumComment comment) {
        UserBean user = findUser(comment.getUserId());

        AdminCommentDTO dto = new AdminCommentDTO();

        dto.setCommentId(comment.getCommentId());
        dto.setPostId(comment.getPostId());
        dto.setUserId(comment.getUserId());
        dto.setUserName(resolveUserName(user));
        dto.setUserEmail(resolveUserEmail(user));
        dto.setContent(comment.getContent());
        dto.setStatus(comment.getStatus());

        return dto;
    }

    private AdminReportDTO toAdminReportDTO(Report report) {
        UserBean reporter = findUser(report.getReporterId());
        UserBean reported = findUser(report.getReportedUserId());

        AdminReportDTO dto = new AdminReportDTO();

        dto.setReportId(report.getReportId());

        dto.setReporterId(report.getReporterId());
        dto.setReporterUserName(resolveUserName(reporter));
        dto.setReporterUserEmail(resolveUserEmail(reporter));

        dto.setReportedUserId(report.getReportedUserId());
        dto.setReportedUserName(resolveUserName(reported));
        dto.setReportedUserEmail(resolveUserEmail(reported));

        dto.setPostId(report.getPostId());
        dto.setCommentId(report.getCommentId());
        dto.setReason(report.getReason());
        dto.setStatus(report.getStatus());
        dto.setCreatedAt(report.getCreatedAt());

        return dto;
    }

    private UserBean findUser(Long userId) {
        if (userId == null) {
            return null;
        }

        return usersRepository.findById(userId).orElse(null);
    }

    private String resolveUserName(UserBean user) {
        if (user == null) {
            return "未知會員";
        }

        if (user.getProfiles() != null && user.getProfiles().getNickname() != null
                && !user.getProfiles().getNickname().isBlank()) {
            return user.getProfiles().getNickname();
        }

        if (user.getProfiles() != null && user.getProfiles().getFullName() != null
                && !user.getProfiles().getFullName().isBlank()) {
            return user.getProfiles().getFullName();
        }

        return "會員 " + user.getId();
    }

    private String resolveUserEmail(UserBean user) {
        if (user == null) {
            return null;
        }

        return user.getEmail();
    }
}