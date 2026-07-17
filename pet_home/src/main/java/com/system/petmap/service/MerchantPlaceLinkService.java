package com.system.petmap.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.system.member.exception.BusinessException;
import com.system.petmap.dto.MerchantPlaceLinkCreateRequest;
import com.system.petmap.entity.MerchantPlaceLink;
import com.system.petmap.entity.MerchantPlaceLinkId;
import com.system.petmap.repository.MerchantPlaceLinkRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MerchantPlaceLinkService {

        private final MerchantPlaceLinkRepository merchantPlaceLinkRepository;

        public MerchantPlaceLink create(
                        MerchantPlaceLinkCreateRequest request) {

                MerchantPlaceLinkId id = new MerchantPlaceLinkId();

                id.setMerchantId(
                                request.getMerchantId());

                id.setPlaceId(
                                request.getPlaceId());

                MerchantPlaceLink link = new MerchantPlaceLink();

                link.setId(id);

                return merchantPlaceLinkRepository
                                .save(link);
        }

        public List<MerchantPlaceLink> findByMerchantId(
                        Long merchantId) {

                return merchantPlaceLinkRepository
                                .findByIdMerchantId(
                                                merchantId);
        }

        public void delete(
                        Long merchantId,
                        Integer placeId) {

                MerchantPlaceLinkId id = new MerchantPlaceLinkId();

                id.setMerchantId(
                                merchantId);

                id.setPlaceId(
                                placeId);

                MerchantPlaceLink link = merchantPlaceLinkRepository
                                .findById(id)
                                .orElseThrow(() -> new BusinessException(
                                                "MERCHANT_PLACE_LINK_NOT_FOUND",
                                                "找不到商家與景點關聯"));

                merchantPlaceLinkRepository.delete(link);
        }
}