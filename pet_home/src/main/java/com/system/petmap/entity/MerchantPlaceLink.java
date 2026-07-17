package com.system.petmap.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "map_merchant_place_links")
public class MerchantPlaceLink {

    @EmbeddedId
    private MerchantPlaceLinkId id;
}
