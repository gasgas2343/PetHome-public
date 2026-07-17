package com.system.petmap.entity;

import java.io.Serializable;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Data
@Embeddable
public class SubmissionTagId
        implements Serializable {

    private Integer submissionId;

    private Integer tagId;
}
