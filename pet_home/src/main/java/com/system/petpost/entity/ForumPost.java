package com.system.petpost.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.HashSet;
import java.util.Set;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import lombok.Data;

@Data
@Entity
@Table(name = "forum_posts")
public class ForumPost {

    // 文章編號
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long postId;

    // 分類編號
    @Column(name = "category_id")
    private Long categoryId;
    // 會員編號
    @Column(name = "user_id")
    private Long userId;
    // 文章標題
    @Column(name = "title", nullable = false, length = 200)
    private String title;
    // 中文註解：文章標籤，多對多關聯
    // 一篇文章可以有多個標籤，一個標籤也可以被多篇文章使用
    @ManyToMany
    @JoinTable(name = "forum_post_tags", joinColumns = @JoinColumn(name = "post_id"), inverseJoinColumns = @JoinColumn(name = "tag_id"))
    private Set<Tag> tags = new HashSet<>();
    // 文章內容
    @Column(name = "content", nullable = false)
    private String content;
    // Cloudinary 封面圖片網址
    @Column(name = "cover_image_url", length = 500)
    private String coverImageUrl;
    // 瀏覽數
    @Column(name = "view_count")
    private Integer viewCount = 0;
    // 按讚數
    @Column(name = "like_count")
    private Integer likeCount = 0;
    // 收藏數
    @Column(name = "favorite_count")
    private Integer favoriteCount = 0;
    // 留言數
    @Column(name = "comment_count")
    private Integer commentCount = 0;
    // 熱門分數
    @Column(name = "trending_score")
    private Double trendingScore = 0.0;
    // 是否置頂
    @Column(name = "is_pinned")
    private Boolean isPinned = false;
    // 是否鎖定
    @Column(name = "is_locked")
    private Boolean isLocked = false;
    // 文章狀態
    // 1 = 正常
    // 2 = 封鎖
    // 3 = 刪除
    @Column(name = "status")
    private Byte status = 1;
    // 封鎖原因
    @Column(name = "blocked_reason")
    private String blockedReason;
    // 封鎖時間
    @Column(name = "blocked_at")
    private LocalDateTime blockedAt;
    // 封鎖管理員ID
    @Column(name = "blocked_by")
    private Long blockedBy;
    // 刪除時間
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
    // 建立時間
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    // 更新時間
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
