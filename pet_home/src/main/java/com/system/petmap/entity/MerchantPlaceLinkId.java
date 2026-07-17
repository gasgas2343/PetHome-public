package com.system.petmap.entity;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

@Data
@Embeddable
public class MerchantPlaceLinkId
        implements Serializable {

    @Column(name = "merchant_id")
    private Long merchantId;

    @Column(name = "place_id")
    private Integer placeId;
}
