package com.system.petmap.dto;

import lombok.Data;

@Data
public class MerchantApplicationHandleRequest {

    private Long handledBy;

    private String adminNote;
}