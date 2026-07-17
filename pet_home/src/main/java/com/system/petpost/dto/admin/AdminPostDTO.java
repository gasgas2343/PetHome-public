package com.system.petpost.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 中文註解：論壇後台文章管理 DTO。
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdminPostDTO {

    private Long postId;
    private Long userId;

    private Integer viewCount;
    private Integer likeCount;
    
    // 中文註解：作者顯示名稱。
    private String userName;
    private String title;
    // 中文註解：作者帳號，前端會遮罩顯示。
    private String userEmail;

    private Byte status;

    private Boolean canDelete;
}