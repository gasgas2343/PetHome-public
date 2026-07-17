package com.system.petmap.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.system.petmap.dto.ReportCreateRequest;
import com.system.petmap.dto.ReportHandleRequest;
import com.system.petmap.entity.Report;
import com.system.member.exception.BusinessException;
import com.system.petmap.repository.PlaceRepository;
import com.system.petmap.repository.PetMapReportRepository;
import com.system.petmap.repository.ReviewRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReportService {

        private final PetMapReportRepository reportRepository;

        private final PlaceRepository placeRepository;

        private final ReviewRepository reviewRepository;

        public Report create(
                        ReportCreateRequest request) {

                if (request.getPlaceId() != null) {
                        placeRepository.findById(request.getPlaceId())
                                        .orElseThrow(() -> new BusinessException(
                                                        "PLACE_NOT_FOUND",
                                                        "找不到景點"));
                }

                if (request.getReviewId() != null) {
                        reviewRepository.findById(request.getReviewId())
                                        .orElseThrow(() -> new BusinessException(
                                                        "REVIEW_NOT_FOUND",
                                                        "找不到評論"));
                }

                if (request.getPlaceId() == null
                                && request.getReviewId() == null) {

                        throw new BusinessException(
                                        "REPORT_TARGET_REQUIRED",
                                        "必須指定檢舉地點或檢舉評論");
                }

                Report report = new Report();

                report.setReporterId(
                                request.getReporterId());

                report.setPlaceId(
                                request.getPlaceId());

                report.setReviewId(
                                request.getReviewId());

                report.setReason(
                                request.getReason());

                report.setDescription(
                                request.getDescription());

                report.setStatus(
                                "PENDING");

                report.setCreatedAt(
                                LocalDateTime.now());

                return reportRepository.save(
                                report);
        }

        public List<Report> findByReporterId(
                        Long reporterId) {

                return reportRepository
                                .findByReporterId(
                                                reporterId);
        }

        public List<Report> findPendingReports() {

                return reportRepository
                                .findByStatus(
                                                "PENDING");
        }

        public Report approve(
                        Integer reportId,
                        ReportHandleRequest request) {
                Report report = reportRepository
                                .findById(reportId)
                                .orElseThrow(() -> new BusinessException(
                                                "REPORT_NOT_FOUND",
                                                "找不到檢舉"));

                if (!"PENDING".equals(
                                report.getStatus())) {

                        throw new BusinessException(
                                        "REPORT_ALREADY_HANDLED",
                                        "此檢舉已處理");
                }
                report.setStatus(
                                "APPROVED");

                report.setAdminNote(
                                request.getAdminNote());

                report.setHandledBy(
                                request.getHandledBy());

                report.setHandledAt(
                                LocalDateTime.now());
                return reportRepository.save(
                                report);
        }

        public Report reject(
                        Integer reportId,
                        ReportHandleRequest request) {

                Report report = reportRepository
                                .findById(reportId)
                                .orElseThrow(() -> new BusinessException(
                                                "REPORT_NOT_FOUND",
                                                "找不到檢舉"));

                if (!"PENDING".equals(
                                report.getStatus())) {

                        throw new BusinessException(
                                        "REPORT_ALREADY_HANDLED",
                                        "此檢舉已處理");
                }

                report.setStatus(
                                "REJECTED");

                report.setAdminNote(
                                request.getAdminNote());

                report.setHandledBy(
                                request.getHandledBy());

                report.setHandledAt(
                                LocalDateTime.now());

                return reportRepository.save(
                                report);
        }

        public Map<String, Long> getStatistics() {

                Map<String, Long> statistics = new HashMap<>();

                statistics.put(
                                "pending",
                                reportRepository.countByStatus(
                                                "PENDING"));

                statistics.put(
                                "approved",
                                reportRepository.countByStatus(
                                                "APPROVED"));

                statistics.put(
                                "rejected",
                                reportRepository.countByStatus(
                                                "REJECTED"));

                return statistics;
        }

        public List<Report> findAll() {

                return reportRepository.findAll();
        }

}
