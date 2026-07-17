package com.system.petmap.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.system.petmap.entity.PlaceSubmission;

public interface PlaceSubmissionRepository
                extends JpaRepository<PlaceSubmission, Integer> {

        List<PlaceSubmission> findByUserId(
                        Long userId);


        List<PlaceSubmission> findByStatus(
        String status);

        long countByStatus(String status);
}