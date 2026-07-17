
package com.system.petpost.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

@Data
public class PostResponseDTO {

    // 文章ID
    private Integer postId;

    // 標題
    private String title;

    // 內容
    private String content;

    // 作者
    private String userName;

    // Cloudinary 封面圖片網址
    private String coverImageUrl;

    // ===================
    // 多張圖片
    // forum_post_images
    // ===================
    private List<String> imageUrls;

    // ===================
    // 按讚數
    // ===================
    private Integer likeCount;

    // 是否已按讚
    private Boolean liked;

    // ===================
    // 收藏數
    // ===================
    private Integer favoriteCount;

    // 是否已收藏
    private Boolean favorited;

    // ===================
    // 留言數
    // ===================
    private Integer commentCount;

    // 建立時間
    private LocalDateTime createdTime;

}