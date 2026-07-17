package com.system.petmap.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.system.petmap.dto.MerchantReviewReplyCreateRequest;
import com.system.petmap.dto.MerchantReviewReplyUpdateRequest;
import com.system.petmap.entity.MerchantReviewReply;
import com.system.member.exception.BusinessException;
import com.system.petmap.repository.MerchantReviewReplyRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MerchantReviewReplyService {

        private final MerchantReviewReplyRepository merchantReviewReplyRepository;

        public MerchantReviewReply create(
                        MerchantReviewReplyCreateRequest request) {

                if (merchantReviewReplyRepository
                                .findFirstByReviewId(
                                                request.getReviewId())
                                .isPresent()) {

                        throw new BusinessException(
                                        "MERCHANT_REVIEW_ALREADY_REPLIED",
                                        "此評論已回覆過");
                }

                MerchantReviewReply reply = new MerchantReviewReply();

                reply.setReviewId(
                                request.getReviewId());

                reply.setMerchantId(
                                request.getMerchantId());

                reply.setReplyContent(
                                request.getReplyContent());

                LocalDateTime now = LocalDateTime.now();

                reply.setCreatedAt(now);

                reply.setUpdatedAt(now);

                return merchantReviewReplyRepository
                                .save(reply);
        }

        public List<MerchantReviewReply> findByReviewId(
                        Integer reviewId) {

                return merchantReviewReplyRepository
                                .findByReviewId(
                                                reviewId);
        }

        public MerchantReviewReply update(
                        Integer replyId,
                        MerchantReviewReplyUpdateRequest request) {

                MerchantReviewReply reply = merchantReviewReplyRepository
                                .findById(replyId)
                                .orElseThrow(() -> new BusinessException(
                                                "MERCHANT_REVIEW_REPLY_NOT_FOUND",
                                                "找不到商家回覆"));

                reply.setReplyContent(
                                request.getReplyContent());

                LocalDateTime now = LocalDateTime.now();

                reply.setUpdatedAt(now);

                return merchantReviewReplyRepository
                                .save(reply);
        }

        public void delete(
                        Integer replyId) {

                MerchantReviewReply reply = merchantReviewReplyRepository
                                .findById(replyId)
                                .orElseThrow(() -> new BusinessException(
                                                "MERCHANT_REVIEW_REPLY_NOT_FOUND",
                                                "找不到商家回覆"));

                merchantReviewReplyRepository
                                .delete(reply);
        }

}
