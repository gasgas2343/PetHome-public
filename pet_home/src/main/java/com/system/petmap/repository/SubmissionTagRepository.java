package com.system.petmap.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.system.petmap.entity.SubmissionTag;
import com.system.petmap.entity.SubmissionTagId;

public interface SubmissionTagRepository
                extends JpaRepository<SubmissionTag, SubmissionTagId> {

        List<SubmissionTag> findByIdSubmissionId(
                        Integer submissionId);

        void deleteByIdSubmissionId(
                        Integer submissionId);
}
