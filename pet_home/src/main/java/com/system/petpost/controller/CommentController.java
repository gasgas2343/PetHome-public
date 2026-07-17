package com.system.petpost.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.system.member.security.annotation.RequirePermission;
import com.system.petpost.dto.CommentCreateDTO;
import com.system.petpost.dto.CommentUpdateDTO;
import com.system.petpost.entity.ForumComment;
import com.system.petpost.service.CommentService;
import com.system.petpost.util.LoginUserUtil;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    /**
     * 中文註解：新增留言。
     * userId 不再由前端傳入，改從 JWT 取得目前登入者。
     */
    @PostMapping
    @RequirePermission("FORUM_COMMENT_CREATE")
    public ResponseEntity<ForumComment> create(
            @Valid @RequestBody CommentCreateDTO dto) {

        Long userId = LoginUserUtil.getLoginUserId();

        return ResponseEntity.ok(
                commentService.create(dto, userId));
    }

    /**
     * 中文註解：查詢文章留言，公開查詢 API。
     */
    @GetMapping("/post/{postId}")
    public ResponseEntity<List<ForumComment>> findByPostId(
            @PathVariable Long postId) {

        return ResponseEntity.ok(
                commentService.findByPostId(postId));
    }

    /**
     * 中文註解：查詢單筆留言，公開查詢 API。
     */
    @GetMapping("/{commentId}")
    public ResponseEntity<ForumComment> findById(
            @PathVariable Long commentId) {

        return ResponseEntity.ok(
                commentService.findById(commentId));
    }

    /**
     * 中文註解：查詢留言回覆，公開查詢 API。
     */
    @GetMapping("/{commentId}/replies")
    public ResponseEntity<List<ForumComment>> findReplies(
            @PathVariable Long commentId) {

        return ResponseEntity.ok(
                commentService.findReplies(commentId));
    }

    /**
     * 中文註解：修改留言。
     * 權限檢查：是否有修改留言權限。
     * 本人檢查：由 Service 確認只能修改自己的留言。
     */
    @PutMapping("/{commentId}")
    @RequirePermission("FORUM_COMMENT_UPDATE")
    public ResponseEntity<ForumComment> update(
            @PathVariable Long commentId,
            @Valid @RequestBody CommentUpdateDTO dto) {

        Long userId = LoginUserUtil.getLoginUserId();

        return ResponseEntity.ok(
                commentService.update(commentId, userId, dto));
    }

    /**
     * 中文註解：刪除留言。
     * 權限檢查：是否有刪除留言權限。
     * 本人檢查：由 Service 確認只能刪除自己的留言。
     */
    @DeleteMapping("/{commentId}")
    @RequirePermission("FORUM_COMMENT_DELETE")
    public ResponseEntity<String> delete(
            @PathVariable Long commentId) {

        Long userId = LoginUserUtil.getLoginUserId();

        commentService.delete(commentId, userId);

        return ResponseEntity.ok("留言刪除成功");
    }
}