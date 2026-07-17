package com.system.petmap.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.system.member.exception.BusinessException;
import com.system.petmap.dto.MerchantUpdateRequest;
import com.system.petmap.entity.Merchant;
import com.system.petmap.entity.MerchantPlaceLink;
import com.system.petmap.entity.Review;
import com.system.petmap.repository.MerchantPlaceLinkRepository;
import com.system.petmap.repository.MerchantRepository;
import com.system.petmap.repository.ReviewRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MerchantService {

        private final MerchantRepository merchantRepository;

        private final MerchantPlaceLinkRepository merchantPlaceLinkRepository;

        private final ReviewRepository reviewRepository;

        public List<Merchant> findByOwnerUserId(
                        Long userId) {

                return merchantRepository
                                .findByOwnerUserId(userId);
        }

        public Merchant getById(
                        Long merchantId) {

                return merchantRepository
                                .findById(merchantId)
                                .orElseThrow(() -> new BusinessException(
                                                "MERCHANT_NOT_FOUND",
                                                "找不到商家"));
        }

        public Merchant update(
                        Long merchantId,
                        MerchantUpdateRequest request) {

                Merchant merchant = merchantRepository
                                .findById(merchantId)
                                .orElseThrow(() -> new BusinessException(
                                                "MERCHANT_NOT_FOUND",
                                                "找不到商家"));

                merchant.setMerchantName(
                                request.getMerchantName());

                merchant.setMerchantType(
                                request.getMerchantType());

                merchant.setDescription(
                                request.getDescription());

                merchant.setPhone(
                                request.getPhone());

                merchant.setEmail(
                                request.getEmail());

                merchant.setCity(
                                request.getCity());

                merchant.setDistrict(
                                request.getDistrict());

                merchant.setAddress(
                                request.getAddress());

                merchant.setLatitude(
                                request.getLatitude());

                merchant.setLongitude(
                                request.getLongitude());

                merchant.setBusinessHours(
                                request.getBusinessHours());

                merchant.setImageUrl(
                                request.getImageUrl());

                merchant.setStatus(
                                request.getStatus());

                LocalDateTime now = LocalDateTime.now();

                merchant.setUpdatedAt(now);

                return merchantRepository.save(
                                merchant);
        }

        public Merchant deactivate(
                        Long merchantId) {

                Merchant merchant = merchantRepository
                                .findById(merchantId)
                                .orElseThrow(() -> new BusinessException(
                                                "MERCHANT_NOT_FOUND",
                                                "找不到商家"));

                LocalDateTime now = LocalDateTime.now();

                merchant.setStatus("INACTIVE");

                merchant.setUpdatedAt(now);

                return merchantRepository.save(
                                merchant);
        }

        public List<Review> getMerchantReviews(
                        Long merchantId) {

                List<MerchantPlaceLink> links = merchantPlaceLinkRepository
                                .findByIdMerchantId(
                                                merchantId);

                List<Review> reviews = new ArrayList<>();

                for (MerchantPlaceLink link : links) {

                        Integer placeId = link.getId()
                                        .getPlaceId();

                        reviews.addAll(
                                        reviewRepository
                                                        .findByPlaceId(
                                                                        placeId));
                }

                return reviews;
        }
}
