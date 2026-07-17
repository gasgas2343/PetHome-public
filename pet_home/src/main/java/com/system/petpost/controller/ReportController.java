package com.system.petpost.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.system.member.dto.ApiResponse;
import com.system.member.security.annotation.RequirePermission;
import com.system.petpost.dto.ReportCreateDTO;
import com.system.petpost.entity.Report;
import com.system.petpost.service.ReportService;
import com.system.petpost.util.LoginUserUtil;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    /**
     * 中文註解：建立檢舉。
     * reporterId 不再由前端傳入，改從 JWT 取得目前登入者。
     */
    @PostMapping
    @RequirePermission("FORUM_REPORT_CREATE")
    public ResponseEntity<ApiResponse<Report>> createReport(
            @RequestBody ReportCreateDTO dto) {

        Long reporterId = LoginUserUtil.getLoginUserId();

        dto.setReporterId(reporterId);

        Report report = reportService.createReport(dto);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "REPORT_CREATED",
                        "檢舉成功",
                        report));
    }

    /**
     * 中文註解：查詢所有檢舉。
     * 管理員/員工權限。
     */
    @GetMapping
    @RequirePermission("FORUM_REPORT_REVIEW")
    public ResponseEntity<ApiResponse<List<Report>>> findAll() {

        List<Report> reports = reportService.findAll();

        return ResponseEntity.ok(
                ApiResponse.success(
                        "REPORT_LIST_SUCCESS",
                        "查詢檢舉成功",
                        reports));
    }

    /**
     * 中文註解：查詢單筆檢舉。
     * 管理員/員工權限。
     */
    @GetMapping("/{reportId}")
    @RequirePermission("FORUM_REPORT_REVIEW")
    public ResponseEntity<ApiResponse<Report>> findById(
            @PathVariable Long reportId) {

        Report report = reportService.findById(reportId);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "REPORT_DETAIL_SUCCESS",
                        "查詢檢舉成功",
                        report));
    }

    /**
     * 中文註解：查詢待審核檢舉。
     * 管理員/員工權限。
     */
    @GetMapping("/pending")
    @RequirePermission("FORUM_REPORT_REVIEW")
    public ResponseEntity<ApiResponse<List<Report>>> findPendingReports() {

        List<Report> reports = reportService.findPendingReports();

        return ResponseEntity.ok(
                ApiResponse.success(
                        "REPORT_PENDING_SUCCESS",
                        "查詢待審核檢舉成功",
                        reports));
    }

    /**
     * 中文註解：檢舉成立。
     * adminId 不再由前端傳入，改從 JWT 取得目前登入管理員/員工。
     */
    @PutMapping("/{reportId}/approve")
    @RequirePermission("FORUM_REPORT_REVIEW")
    public ResponseEntity<ApiResponse<Report>> approve(
            @PathVariable Long reportId,
            @RequestParam String note) {

        Long adminId = LoginUserUtil.getLoginUserId();

        Report report = reportService.approve(reportId, adminId, note);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "REPORT_APPROVED",
                        "檢舉成立",
                        report));
    }

    /**
     * 中文註解：檢舉駁回。
     * adminId 不再由前端傳入，改從 JWT 取得目前登入管理員/員工。
     */
    @PutMapping("/{reportId}/reject")
    @RequirePermission("FORUM_REPORT_REVIEW")
    public ResponseEntity<ApiResponse<Report>> reject(
            @PathVariable Long reportId,
            @RequestParam String note) {

        Long adminId = LoginUserUtil.getLoginUserId();

        Report report = reportService.reject(reportId, adminId, note);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "REPORT_REJECTED",
                        "檢舉已駁回",
                        report));
    }
}