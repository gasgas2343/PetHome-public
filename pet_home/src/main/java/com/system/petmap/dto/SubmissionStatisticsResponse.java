package com.system.petmap.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SubmissionStatisticsResponse {

    private long pending;

    private long approved;

    private long rejected;

    private long needRevision;
}
