package com.system.petpost.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class CommentResponseDTO {

    // 留言ID
    private Integer commentId;

    // 所屬文章
    private Integer postId;

    // 作者ID
    private Integer userId;

    // 作者名稱
    private String userName;

    // 留言內容
    private String content;

    // ===================
    // 按讚數
    // ===================
    private Integer likeCount;

    // 是否已按讚
    private Boolean liked;

    // 建立時間
    private LocalDateTime createdTime;

}
