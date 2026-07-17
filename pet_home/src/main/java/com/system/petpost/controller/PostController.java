package com.system.petpost.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.system.member.security.annotation.RequirePermission;
import com.system.petpost.dto.PostCreateDTO;
import com.system.petpost.dto.PostUpdateDTO;
import com.system.petpost.entity.ForumPost;
import com.system.petpost.service.PostService;
import com.system.petpost.util.LoginUserUtil;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    /**
     * 中文註解：新增文章。
     * 不再從前端接 userId，改從 JWT token 取得目前登入者。
     */
    @PostMapping
    @RequirePermission("FORUM_POST_CREATE")
    public ResponseEntity<ForumPost> create(
            @Valid @RequestBody PostCreateDTO dto) {

        Long userId = LoginUserUtil.getLoginUserId();

        return ResponseEntity.ok(
                postService.create(dto, userId));
    }

    /**
     * 中文註解：查詢所有文章。
     * 公開查詢 API，暫時不加權限。
     */
    @GetMapping
    public ResponseEntity<List<ForumPost>> findAll() {

        return ResponseEntity.ok(
                postService.findAll());
    }

    /**
     * 中文註解：查詢熱門文章。
     * 公開查詢 API，暫時不加權限。
     */
    @GetMapping("/hot")
    public ResponseEntity<List<ForumPost>> findHotPosts() {

        return ResponseEntity.ok(
                postService.findHotPosts());
    }

    /**
     * 中文註解：查詢文章詳細資料。
     * 公開查詢 API，暫時不加權限。
     */
    @GetMapping("/{postId}")
    public ResponseEntity<ForumPost> findById(
            @PathVariable Long postId) {

        return ResponseEntity.ok(
                postService.findPostDetail(postId));
    }

    /**
     * 中文註解：修改文章。
     * 先檢查使用者是否有修改文章權限，再由 Service 檢查是否為文章本人。
     */
    @PutMapping("/{postId}")
    @RequirePermission("FORUM_POST_UPDATE")
    public ResponseEntity<ForumPost> update(
            @PathVariable Long postId,
            @Valid @RequestBody PostUpdateDTO dto) {

        Long userId = LoginUserUtil.getLoginUserId();

        return ResponseEntity.ok(
                postService.update(postId, dto, userId));
    }

    /**
     * 中文註解：刪除文章。
     * 先檢查使用者是否有刪除文章權限，再由 Service 檢查是否為文章本人。
     */
    @DeleteMapping("/{postId}")
    @RequirePermission("FORUM_POST_DELETE")
    public ResponseEntity<String> delete(
            @PathVariable Long postId) {

        Long userId = LoginUserUtil.getLoginUserId();

        postService.delete(postId, userId);

        return ResponseEntity.ok("文章刪除成功");
    }

    /**
     * 中文註解：管理員後台強制刪除文章。
     * 與會員刪除不同，這裡不檢查文章擁有者。
     */
    @DeleteMapping("/{postId}/admin")
    @RequirePermission("FORUM_ADMIN_POST_MANAGE")
    public ResponseEntity<String> adminDelete(
            @PathVariable Long postId) {

        postService.adminDelete(postId);

        return ResponseEntity.ok("管理員已刪除文章");
    }

    /**
     * 中文註解：管理員鎖定文章。
     */
    @PutMapping("/{postId}/lock")
    @RequirePermission("FORUM_ADMIN_POST_MANAGE")
    public ResponseEntity<String> lockPost(
            @PathVariable Long postId) {

        postService.lockPost(postId);

        return ResponseEntity.ok("文章已鎖定");
    }

    /**
     * 中文註解：管理員解除鎖定文章。
     */
    @PutMapping("/{postId}/unlock")
    @RequirePermission("FORUM_ADMIN_POST_MANAGE")
    public ResponseEntity<String> unlockPost(
            @PathVariable Long postId) {

        postService.unlockPost(postId);

        return ResponseEntity.ok("文章已解除鎖定");
    }
}