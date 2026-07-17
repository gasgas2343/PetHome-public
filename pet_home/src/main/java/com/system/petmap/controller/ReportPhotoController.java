package com.system.petmap.controller;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.system.member.dto.ApiResponse;
import com.system.member.security.annotation.RequirePermission;
import com.system.petmap.dto.ReportPhotoCreateRequest;
import com.system.petmap.entity.ReportPhoto;
import com.system.petmap.service.ReportPhotoService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/map/report-photos")
@RequiredArgsConstructor
public class ReportPhotoController {

        private final ReportPhotoService reportPhotoService;

        @RequirePermission("REPORT_PHOTO_CREATE")
        @PostMapping
        public ApiResponse<ReportPhoto> create(
                        @RequestBody ReportPhotoCreateRequest request) {

                return ApiResponse.success(
                                "REPORT_PHOTO_CREATE_SUCCESS",
                                "新增檢舉照片成功",
                                reportPhotoService.create(request));
        }

        @RequirePermission("REPORT_PHOTO_DELETE")
        @DeleteMapping("/{photoId}")
        public ApiResponse<Void> delete(
                        @PathVariable Integer photoId) {

                reportPhotoService.delete(photoId);

                return ApiResponse.success(
                                "REPORT_PHOTO_DELETE_SUCCESS",
                                "刪除檢舉照片成功",
                                null);
        }

        @RequirePermission("REPORT_PHOTO_LIST")
        @GetMapping("/report/{reportId}")
        public ApiResponse<List<ReportPhoto>> getByReportId(
                        @PathVariable Integer reportId) {

                return ApiResponse.success(
                                "REPORT_PHOTO_LIST_SUCCESS",
                                "取得檢舉照片成功",
                                reportPhotoService.getByReportId(reportId));
        }
}
