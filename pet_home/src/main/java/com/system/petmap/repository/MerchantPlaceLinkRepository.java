package com.system.petmap.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.system.petmap.entity.MerchantPlaceLink;
import com.system.petmap.entity.MerchantPlaceLinkId;

public interface MerchantPlaceLinkRepository
        extends JpaRepository<
                MerchantPlaceLink,
                MerchantPlaceLinkId> {

    List<MerchantPlaceLink> findByIdMerchantId(
            Long merchantId);

    List<MerchantPlaceLink> findByIdPlaceId(
            Integer placeId);
}
