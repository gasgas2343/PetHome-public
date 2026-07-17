package com.system.petmap.dto;

import lombok.Data;

@Data
public class ReportHandleRequest {

    private Long handledBy;

    private String adminNote;
}