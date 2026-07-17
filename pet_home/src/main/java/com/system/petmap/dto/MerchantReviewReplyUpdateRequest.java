package com.system.petmap.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class MerchantReviewReplyUpdateRequest {

    @NotBlank(message = "回覆內容不能空白")
    private String replyContent;
}
