package com.system.petpost.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

// 修改留言 DTO
@Data
public class CommentUpdateDTO {

    // 留言內容
    @NotBlank(message = "留言內容不可為空")
    @Size(
            min = 2,
            max = 500,
            message = "留言長度需介於2~500字")
    private String content;

}