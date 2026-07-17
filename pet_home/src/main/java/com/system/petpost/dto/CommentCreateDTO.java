package com.system.petpost.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

// 建立留言 DTO
@Data
public class CommentCreateDTO {

    // 文章編號
    @NotNull(message = "文章編號不可為空")
    private Long postId;

    // 父留言編號
    // 第一層留言可為 null
    private Long parentCommentId;

    // 留言內容
    @NotBlank(message = "留言內容不可為空")
    @Size(
            min = 2,
            max = 500,
            message = "留言長度需介於2~500字")
    private String content;

}