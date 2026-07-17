package com.system.petmap.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "map_submission_tags")
public class SubmissionTag {

    @EmbeddedId
    private SubmissionTagId id;
}