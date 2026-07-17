package com.system.petmap.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.system.petmap.dto.MerchantApplicationCreateRequest;
import com.system.petmap.dto.MerchantApplicationHandleRequest;
import com.system.petmap.entity.Merchant;
import com.system.petmap.entity.MerchantApplication;
import com.system.member.exception.BusinessException;
import com.system.petmap.repository.MerchantApplicationRepository;
import com.system.petmap.repository.MerchantRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MerchantApplicationService {

        private final MerchantApplicationRepository merchantApplicationRepository;

        private final MerchantRepository merchantRepository;

        public MerchantApplication create(
                        MerchantApplicationCreateRequest request) {

                MerchantApplication application = new MerchantApplication();

                application.setUserId(
                                request.getUserId());

                application.setMerchantName(
                                request.getMerchantName());

                application.setTaxId(
                                request.getTaxId());

                application.setBusinessLicenseUrl(
                                request.getBusinessLicenseUrl());

                application.setPlaceName(
                                request.getPlaceName());

                application.setPlaceAddress(
                                request.getPlaceAddress());

                application.setStatus(
                                "PENDING");

                LocalDateTime now = LocalDateTime.now();

                application.setCreatedAt(now);

                return merchantApplicationRepository
                                .save(application);
        }

        public List<MerchantApplication> findByUserId(
                        Long userId) {

                return merchantApplicationRepository
                                .findByUserId(userId);
        }

        public List<MerchantApplication> findPending() {

                return merchantApplicationRepository
                                .findByStatus(
                                                "PENDING");
        }

        public MerchantApplication approve(
                        Integer applicationId,
                        MerchantApplicationHandleRequest request) {

                MerchantApplication application = merchantApplicationRepository
                                .findById(applicationId)
                                .orElseThrow(() -> new BusinessException(
                                                "MERCHANT_APPLICATION_NOT_FOUND",
                                                "找不到商家申請"));

                if (!"PENDING".equals(
                                application.getStatus())) {

                        throw new BusinessException(
                                        "MERCHANT_APPLICATION_ALREADY_REVIEWED",
                                        "此申請已處理");
                }

                LocalDateTime now = LocalDateTime.now();

                application.setStatus(
                                "APPROVED");

                application.setAdminNote(
                                request.getAdminNote());

                application.setReviewedAt(now);

                Merchant merchant = new Merchant();

                merchant.setOwnerUserId(
                                application.getUserId());

                merchant.setMerchantName(
                                application.getMerchantName());

                merchant.setAddress(
                                application.getPlaceAddress());

                merchant.setStatus(
                                "ACTIVE");

                merchant.setCreatedAt(now);

                merchant.setUpdatedAt(now);

                merchantRepository.save(
                                merchant);

                return merchantApplicationRepository
                                .save(application);
        }

        public MerchantApplication reject(
                        Integer applicationId,
                        MerchantApplicationHandleRequest request) {

                MerchantApplication application = merchantApplicationRepository
                                .findById(applicationId)
                                .orElseThrow(() -> new BusinessException(
                                                "MERCHANT_APPLICATION_NOT_FOUND",
                                                "找不到商家申請"));

                if (!"PENDING".equals(
                                application.getStatus())) {

                        throw new BusinessException(
                                        "MERCHANT_APPLICATION_ALREADY_REVIEWED",
                                        "此申請已處理");
                }

                application.setStatus(
                                "REJECTED");

                application.setAdminNote(
                                request.getAdminNote());

                LocalDateTime now = LocalDateTime.now();

                application.setReviewedAt(now);

                return merchantApplicationRepository
                                .save(application);
        }
}
