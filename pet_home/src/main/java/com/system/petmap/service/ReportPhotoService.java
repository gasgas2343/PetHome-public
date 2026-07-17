package com.system.petmap.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.system.member.exception.BusinessException;
import com.system.petmap.dto.ReportPhotoCreateRequest;
import com.system.petmap.entity.ReportPhoto;
import com.system.petmap.repository.ReportPhotoRepository;
import com.system.petmap.repository.PetMapReportRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReportPhotoService {

        private final ReportPhotoRepository reportPhotoRepository;

        private final PetMapReportRepository reportRepository;

        public ReportPhoto create(
                        ReportPhotoCreateRequest request) {

                reportRepository.findById(request.getReportId())
                                .orElseThrow(() -> new BusinessException(
                                                "REPORT_NOT_FOUND",
                                                "找不到檢舉"));

                ReportPhoto photo = new ReportPhoto();

                photo.setReportId(
                                request.getReportId());

                photo.setImageUrl(
                                request.getImageUrl());

                photo.setCreatedAt(
                                LocalDateTime.now());

                return reportPhotoRepository.save(
                                photo);
        }

        public void delete(
                        Integer photoId) {

                ReportPhoto photo = reportPhotoRepository
                                .findById(photoId)
                                .orElseThrow(() -> new BusinessException(
                                                "REPORT_PHOTO_NOT_FOUND",
                                                "找不到檢舉照片"));

                reportPhotoRepository.delete(
                                photo);
        }

        public List<ReportPhoto> getByReportId(Integer reportId) {

                return reportPhotoRepository.findByReportId(reportId);
        }
}
