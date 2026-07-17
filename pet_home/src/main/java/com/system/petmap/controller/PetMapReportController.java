package com.system.petmap.controller;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.system.member.dto.ApiResponse;
import com.system.member.security.annotation.RequirePermission;
import com.system.petmap.dto.ReportCreateRequest;
import com.system.petmap.dto.ReportHandleRequest;
import com.system.petmap.entity.Report;
import com.system.petmap.service.ReportService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/map/reports")
@RequiredArgsConstructor
public class PetMapReportController {

        private final ReportService reportService;

        @RequirePermission("REPORT_CREATE")
        @PostMapping
        public ApiResponse<Report> create(
                        @RequestBody ReportCreateRequest request) {

                return ApiResponse.success(
                                "REPORT_CREATE_SUCCESS",
                                "新增檢舉成功",
                                reportService.create(request));
        }

        @RequirePermission("REPORT_LIST_BY_REPORTER")
        @GetMapping("/reporter/{reporterId}")
        public ApiResponse<List<Report>> findByReporterId(
                        @PathVariable Long reporterId) {

                return ApiResponse.success(
                                "REPORT_LIST_SUCCESS",
                                "取得檢舉成功",
                                reportService.findByReporterId(reporterId));
        }

        @RequirePermission("REPORT_PENDING_LIST")
        @GetMapping("/pending")
        public ApiResponse<List<Report>> findPendingReports() {

                return ApiResponse.success(
                                "REPORT_PENDING_LIST_SUCCESS",
                                "取得待處理檢舉成功",
                                reportService.findPendingReports());
        }

        @RequirePermission("REPORT_APPROVE")
        @PutMapping("/{reportId}/approve")
        public ApiResponse<Report> approve(
                        @PathVariable Integer reportId,
                        @RequestBody ReportHandleRequest request) {

                return ApiResponse.success(
                                "REPORT_APPROVE_SUCCESS",
                                "檢舉審核通過",
                                reportService.approve(reportId, request));
        }

        @RequirePermission("REPORT_REJECT")
        @PutMapping("/{reportId}/reject")
        public ApiResponse<Report> reject(
                        @PathVariable Integer reportId,
                        @RequestBody ReportHandleRequest request) {

                return ApiResponse.success(
                                "REPORT_REJECT_SUCCESS",
                                "檢舉已駁回",
                                reportService.reject(reportId, request));
        }

        @RequirePermission("REPORT_STATISTICS")
        @GetMapping("/statistics")
        public ApiResponse<Map<String, Long>> statistics() {

                return ApiResponse.success(
                                "REPORT_STATISTICS_SUCCESS",
                                "取得統計成功",
                                reportService.getStatistics());
        }

        @RequirePermission("REPORT_ALL_LIST")
        @GetMapping
        public ApiResponse<List<Report>> findAll() {

                return ApiResponse.success(
                                "REPORT_ALL_SUCCESS",
                                "取得所有檢舉成功",
                                reportService.findAll());
        }
}
