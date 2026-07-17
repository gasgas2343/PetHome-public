package com.system.petmap.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class PlaceCreateRequest {

    private String name;

    private String placeType;

    private String address;

    private BigDecimal latitude;

    private BigDecimal longitude;

    private String phone;
}
