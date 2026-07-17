package com.system.petmap.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.system.petmap.entity.MerchantReviewReply;

import java.util.Optional;

public interface MerchantReviewReplyRepository
                extends JpaRepository<MerchantReviewReply, Integer> {

        List<MerchantReviewReply> findByReviewId(
                        Integer reviewId);

        List<MerchantReviewReply> findByMerchantId(
                        Long merchantId);

        Optional<MerchantReviewReply> findFirstByReviewId(
                        Integer reviewId);
}
