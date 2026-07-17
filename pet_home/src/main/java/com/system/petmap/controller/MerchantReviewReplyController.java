package com.system.petmap.controller;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.system.member.dto.ApiResponse;
import com.system.member.security.annotation.RequirePermission;
import com.system.petmap.dto.MerchantReviewReplyCreateRequest;
import com.system.petmap.dto.MerchantReviewReplyUpdateRequest;
import com.system.petmap.entity.MerchantReviewReply;
import com.system.petmap.service.MerchantReviewReplyService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/map/merchant-review-replies")
@RequiredArgsConstructor
public class MerchantReviewReplyController {

        private final MerchantReviewReplyService merchantReviewReplyService;

        @RequirePermission("MERCHANT_REPLY_CREATE")
        @PostMapping
        public ApiResponse<MerchantReviewReply> create(
                        @Valid @RequestBody MerchantReviewReplyCreateRequest request) {

                return ApiResponse.success(
                                "MERCHANT_REVIEW_REPLY_CREATE_SUCCESS",
                                "商家回覆成功",
                                merchantReviewReplyService.create(request));
        }

        // @RequirePermission("MERCHANT_REPLY_LIST_BY_REVIEW")
        @GetMapping("/review/{reviewId}")
        public ApiResponse<List<MerchantReviewReply>> findByReviewId(
                        @PathVariable Integer reviewId) {

                return ApiResponse.success(
                                "MERCHANT_REVIEW_REPLY_LIST_SUCCESS",
                                "取得商家回覆成功",
                                merchantReviewReplyService.findByReviewId(reviewId));
        }

        @RequirePermission("MERCHANT_REPLY_UPDATE")
        @PutMapping("/{replyId}")
        public ApiResponse<MerchantReviewReply> update(
                        @PathVariable Integer replyId,
                        @RequestBody MerchantReviewReplyUpdateRequest request) {

                return ApiResponse.success(
                                "MERCHANT_REVIEW_REPLY_UPDATE_SUCCESS",
                                "更新商家回覆成功",
                                merchantReviewReplyService.update(
                                                replyId,
                                                request));
        }

        @RequirePermission("MERCHANT_REPLY_DELETE")
        @DeleteMapping("/{replyId}")
        public ApiResponse<Void> delete(
                        @PathVariable Integer replyId) {

                merchantReviewReplyService.delete(replyId);

                return ApiResponse.success(
                                "MERCHANT_REVIEW_REPLY_DELETE_SUCCESS",
                                "刪除商家回覆成功",
                                null);
        }
}
