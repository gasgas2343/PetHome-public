package com.system.petmap.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.system.petmap.entity.ReportPhoto;

public interface ReportPhotoRepository
                extends JpaRepository<ReportPhoto, Integer> {

        List<ReportPhoto> findByReportId(
                        Integer reportId);

        void deleteByReportId(
                        Integer reportId);
}