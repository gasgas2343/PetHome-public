package com.system.petpost.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 中文註解：論壇後台留言管理 DTO。
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdminCommentDTO {

    private Long commentId;
    private Long postId;
    private Long userId;

    private String userName;
    private String userEmail;

    private String content;
    private Byte status;
}