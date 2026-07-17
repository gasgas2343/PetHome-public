package com.system.petmap.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

@Data
public class SubmissionDetailResponse {

    private Integer submissionId;

    private String name;

    private String placeType;

    private String address;

    private String status;

    private List<Integer> tagIds;

    private List<String> tags;

    private String phone;

    private String description;

    private String adminNote;

    private LocalDateTime createdAt;

    private LocalDateTime reviewedAt;

    private BigDecimal latitude;

    private BigDecimal longitude;
}
