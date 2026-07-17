package com.system.petmap.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.system.petmap.entity.Report;

public interface PetMapReportRepository
                extends JpaRepository<Report, Integer> {

        List<Report> findByReporterId(
                        Long reporterId);

        List<Report> findByStatus(
                        String status);

        long countByStatus(String status);
}