package com.system.petpost.service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.system.petpost.constant.PostStatus;

import com.system.petpost.dto.PostCreateDTO;
import com.system.petpost.dto.PostUpdateDTO;
import com.system.petpost.dto.ReviewResultDTO;

import com.system.petpost.entity.ForumPost;
import com.system.petpost.entity.Tag;

import com.system.petpost.repository.ForumPostRepository;
import com.system.petpost.repository.TagRepository;
import com.system.petpost.util.HotScoreUtil;

import com.system.petpost.constant.ReportStatus;
import com.system.petpost.repository.ReportRepository;

import com.system.member.exception.BusinessException;

import lombok.RequiredArgsConstructor;

// 中文註解：文章服務
@Service
@RequiredArgsConstructor
public class PostService {

    private final ReportRepository reportRepository;

    // 停權檢查
    private final ForumUserStatusService forumUserStatusService;

    // 中文註解：文章 Repository
    private final ForumPostRepository postRepository;

    // 中文註解：AI 審核服務
    private final AiReviewService aiReviewService;

    // 中文註解：標籤 Repository
    private final TagRepository tagRepository;

    // 中文註解：檢舉服務，用於 AI 自動檢舉
    private final ReportService reportService;

    // 中文註解：取得封面圖片，如果 coverImageUrl 沒有值，就取 imageUrls 第一張
    private String resolveCoverImageUrl(String coverImageUrl, List<String> imageUrls) {

        if (coverImageUrl != null && !coverImageUrl.isBlank()) {
            return coverImageUrl;
        }

        if (imageUrls != null && !imageUrls.isEmpty()) {
            return imageUrls.get(0);
        }

        return null;
    }

    // 中文註解：新增文章
    public ForumPost create(PostCreateDTO dto, Long userId) {

        LocalDateTime now = LocalDateTime.now();
        // 中文註解：停權會員不可發文。
        forumUserStatusService.checkForumUsable(userId);

        // 中文註解：AI 審核文章內容
        ReviewResultDTO review = aiReviewService.reviewContent(dto.getContent());

        // 中文註解：判斷 AI 是否封鎖文章
        boolean blockedByAi = "BLOCKED".equals(review.getAiResult());

        ForumPost post = new ForumPost();

        post.setCategoryId(dto.getCategoryId());
        post.setUserId(userId);
        post.setTitle(dto.getTitle());
        post.setContent(dto.getContent());

        post.setCoverImageUrl(
                resolveCoverImageUrl(
                        dto.getCoverImageUrl(),
                        dto.getImageUrls()));

        // 中文註解：綁定文章標籤
        post.setTags(resolveTags(dto.getTags()));

        post.setViewCount(0);
        post.setLikeCount(0);
        post.setFavoriteCount(0);
        post.setCommentCount(0);
        post.setTrendingScore(0.0);

        post.setIsPinned(false);
        post.setIsLocked(false);

        // 中文註解：AI 命中敏感字時，不直接丟掉文章，而是封鎖等待管理員審核
        if (blockedByAi) {

            post.setStatus(PostStatus.BLOCKED);
            post.setBlockedReason(review.getDetectedReason());
            post.setBlockedAt(now);

        } else {

            post.setStatus(PostStatus.ACTIVE);
        }

        post.setCreatedAt(now);
        post.setUpdatedAt(now);

        ForumPost savedPost = postRepository.save(post);

        // 中文註解：AI 封鎖文章後，自動建立檢舉案件
        if (blockedByAi) {

            reportService.createAiPostReport(
                    userId,
                    savedPost.getPostId(),
                    "AI自動檢舉：" + review.getDetectedReason());
        }

        return savedPost;
    }

    // 中文註解：查詢前台文章列表。
    // ACTIVE：正常文章。
    // BLOCKED：被管理員隱藏的文章，前台仍顯示「此文章已被隱藏」。
    public List<ForumPost> findAll() {
        return postRepository.findByStatusInOrderByIsPinnedDescCreatedAtDesc(
                List.of(PostStatus.ACTIVE, PostStatus.BLOCKED));
    }

    // 中文註解：查詢熱門文章。
    // ACTIVE：正常文章。
    // BLOCKED：被管理員隱藏的文章，前台仍顯示「此文章已被隱藏」。
    public List<ForumPost> findHotPosts() {
        return postRepository.findTop10ByStatusInOrderByTrendingScoreDesc(
                List.of(PostStatus.ACTIVE, PostStatus.BLOCKED));
    }

    // 中文註解：查詢單篇文章
    public ForumPost findById(Long postId) {
        if (postId == null) {
            throw new BusinessException("POST_ID_REQUIRED", "文章ID不可為空");
        }
        ForumPost post = postRepository.findById(postId)
                .orElseThrow(() -> new BusinessException("POST_NOT_FOUND", "文章不存在"));
        if (post.getStatus() == PostStatus.DELETED) {
            throw new BusinessException("POST_NOT_FOUND", "文章不存在");
        }
        return post;
    }

    // 中文註解：查詢文章詳情，並增加瀏覽數
    public ForumPost findPostDetail(Long postId) {

        ForumPost post = findById(postId);

        post.setViewCount(post.getViewCount() + 1);

        post.setTrendingScore(
                HotScoreUtil.calculate(
                        post.getLikeCount(),
                        post.getCommentCount(),
                        post.getViewCount()));

        return postRepository.save(post);
    }

    // 中文註解：修改文章
    public ForumPost update(Long postId, PostUpdateDTO dto, Long userId) {
        // 中文註解：停權會員不可修改文章。
        forumUserStatusService.checkForumUsable(userId);

        ForumPost post = findById(postId);
        // 中文註解：只能修改自己的文章。
        // @RequirePermission 只檢查「有沒有修改文章權限」，
        // 這裡檢查「是不是這篇文章的作者」。
        if (!post.getUserId().equals(userId)) {
            throw new BusinessException("POST_OWNER_REQUIRED", "只能修改自己的文章");
        }
        // 中文註解：已鎖定文章不可修改。
        if (Boolean.TRUE.equals(post.getIsLocked())) {
            throw new BusinessException("POST_LOCKED", "文章已被鎖定，無法修改");
        }
        ReviewResultDTO review = aiReviewService.reviewContent(dto.getContent());
        if ("BLOCKED".equals(review.getAiResult())) {
            throw new BusinessException(
                    "POST_CONTENT_BLOCKED",
                    "文章包含違規內容：" + review.getDetectedReason());
        }
        post.setCategoryId(dto.getCategoryId());
        post.setTitle(dto.getTitle());
        post.setContent(dto.getContent());
        post.setCoverImageUrl(
                resolveCoverImageUrl(
                        dto.getCoverImageUrl(),
                        dto.getImageUrls()));
        // 中文註解：修改文章時，也同步更新標籤。
        post.setTags(resolveTags(dto.getTags()));
        post.setUpdatedAt(LocalDateTime.now());
        return postRepository.save(post);
    }

    // 中文註解：軟刪除文章
    public void delete(Long postId, Long userId) {
        ForumPost post = findById(postId);
        // 中文註解：只能刪除自己的文章。
        // 管理員刪除文章建議另外做 adminDeletePost()，不要混在會員刪除裡。
        if (!post.getUserId().equals(userId)) {
            throw new BusinessException("POST_OWNER_REQUIRED", "只能刪除自己的文章");
        }
        if (post.getStatus() == PostStatus.DELETED) {
            throw new BusinessException("POST_ALREADY_DELETED", "文章已刪除");
        }
        post.setStatus(PostStatus.DELETED);
        post.setDeletedAt(LocalDateTime.now());
        post.setUpdatedAt(LocalDateTime.now());
        postRepository.save(post);
    }

    // 中文註解：管理員下架文章
    public ForumPost lockPost(Long postId) {
        ForumPost post = findById(postId);
        post.setIsLocked(true);
        post.setStatus(PostStatus.BLOCKED);
        post.setBlockedAt(LocalDateTime.now());
        post.setUpdatedAt(LocalDateTime.now());

        return postRepository.save(post);
    }

    // 中文註解：管理員恢復文章
    public ForumPost unlockPost(Long postId) {
        ForumPost post = findById(postId);
        post.setIsLocked(false);
        post.setStatus(PostStatus.ACTIVE);
        post.setBlockedReason(null);
        post.setBlockedAt(null);
        post.setBlockedBy(null);
        post.setUpdatedAt(LocalDateTime.now());

        return postRepository.save(post);
    }

    // 中文註解：置頂文章
    public ForumPost pinPost(Long postId) {

        ForumPost post = findById(postId);

        post.setIsPinned(true);
        post.setUpdatedAt(LocalDateTime.now());

        return postRepository.save(post);
    }

    // 中文註解：取消置頂
    public ForumPost unpinPost(Long postId) {

        ForumPost post = findById(postId);

        post.setIsPinned(false);
        post.setUpdatedAt(LocalDateTime.now());

        return postRepository.save(post);
    }

    // 中文註解：將前端傳來的標籤名稱轉成 Tag Entity
    private Set<Tag> resolveTags(List<String> tagNames) {

        Set<Tag> result = new HashSet<>();

        if (tagNames == null || tagNames.isEmpty()) {
            return result;
        }

        for (String tagName : tagNames) {

            if (tagName == null || tagName.isBlank()) {
                continue;
            }

            String cleanName = tagName.trim();

            Tag tag = tagRepository
                    .findByTagName(cleanName)
                    .orElseGet(() -> {
                        Tag newTag = new Tag();
                        newTag.setTagName(cleanName);
                        return tagRepository.save(newTag);
                    });

            result.add(tag);
        }

        return result;
    }

    /**
     * 管理員後台不強制刪除文章，檢舉成功才刪除。
     * 不檢查是否為文章本人，只檢查 Controller 權限。
     */
    public void adminDelete(Long postId) {
        ForumPost post = findById(postId);
        if (post.getStatus() == PostStatus.DELETED) {
            throw new BusinessException("POST_ALREADY_DELETED", "文章已刪除");
        }
        // 只有被隱藏的文章才有資格刪除。
        if (post.getStatus() != PostStatus.BLOCKED) {
            throw new BusinessException(
                    "POST_NOT_BLOCKED",
                    "只有被隱藏的文章才能刪除");
        }
        // 必須有檢舉成立紀錄，管理員才可以刪除。
        boolean hasApprovedReport = reportRepository.existsByPostIdAndStatus(
                postId,
                ReportStatus.APPROVED);
        if (!hasApprovedReport) {
            throw new BusinessException(
                    "REPORT_NOT_APPROVED",
                    "只有檢舉成立的文章才能刪除");
        }
        post.setStatus(PostStatus.DELETED);
        post.setDeletedAt(LocalDateTime.now());
        post.setUpdatedAt(LocalDateTime.now());
        postRepository.save(post);
    }
}