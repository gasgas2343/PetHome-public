package com.system.petpost.controller.admin;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.system.member.security.annotation.RequirePermission;
import com.system.petpost.entity.ForumComment;
import com.system.petpost.repository.CommentRepository;
import com.system.petpost.util.LoginUserUtil;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/admin/comments")
@RequiredArgsConstructor
public class AdminCommentController {

    private final CommentRepository commentRepository;

    // 中文註解：論壇後台查詢全部留言。
    @GetMapping
    @RequirePermission("FORUM_ADMIN_POST_MANAGE")
    public ResponseEntity<List<ForumComment>> findAll() {
        // 中文註解：
        // 後台留言管理只顯示正常與隱藏留言。
        // status = 3 代表會員已刪除，不再顯示於後台列表。
        return ResponseEntity.ok(
                commentRepository.findByStatusNotOrderByCreatedAtDesc((byte) 3));
    }

    // 中文註解：論壇後台封鎖留言。
    @PutMapping("/{commentId}/hide")
    @RequirePermission("FORUM_ADMIN_POST_MANAGE")
    public ResponseEntity<ForumComment> hideComment(@PathVariable Long commentId) {
        ForumComment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("留言不存在"));

        comment.setStatus((byte) 2);
        comment.setBlockedReason("管理員封鎖留言");
        comment.setBlockedAt(LocalDateTime.now());
        comment.setBlockedBy(LoginUserUtil.getLoginUserId());
        comment.setUpdatedAt(LocalDateTime.now());

        return ResponseEntity.ok(commentRepository.save(comment));
    }

    // 中文註解：論壇後台恢復留言。
    @PutMapping("/{commentId}/restore")
    @RequirePermission("FORUM_ADMIN_POST_MANAGE")
    public ResponseEntity<ForumComment> restoreComment(@PathVariable Long commentId) {
        ForumComment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("留言不存在"));

        comment.setStatus((byte) 1);
        comment.setBlockedReason(null);
        comment.setBlockedAt(null);
        comment.setBlockedBy(null);
        comment.setUpdatedAt(LocalDateTime.now());

        return ResponseEntity.ok(commentRepository.save(comment));
    }

    // 中文註解：論壇後台刪除留言。
    @DeleteMapping("/{commentId}")
    @RequirePermission("FORUM_ADMIN_POST_MANAGE")
    public ResponseEntity<String> deleteComment(@PathVariable Long commentId) {
        ForumComment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("留言不存在"));

        commentRepository.delete(comment);

        return ResponseEntity.ok("留言已刪除");
    }

}