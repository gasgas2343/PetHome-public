package com.system.petmap.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MerchantReviewReplyCreateRequest {

    @NotNull
    private Integer reviewId;

    @NotNull
    private Long merchantId;

    @NotBlank(message = "回覆內容不能空白")
    private String replyContent;
    
}
