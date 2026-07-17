package com.system.petmap.controller;

import java.util.List;

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
import com.system.petmap.dto.PlaceSubmissionCreateRequest;
import com.system.petmap.dto.RejectSubmissionRequest;
import com.system.petmap.dto.SubmissionDetailResponse;
import com.system.petmap.dto.SubmissionStatisticsResponse;
import com.system.petmap.entity.PlaceSubmission;
import com.system.petmap.service.PlaceSubmissionService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/map/place-submissions")
@RequiredArgsConstructor
public class PlaceSubmissionController {

        private final PlaceSubmissionService placeSubmissionService;

        @RequirePermission("SUBMISSION_CREATE")
        @PostMapping
        public ApiResponse<PlaceSubmission> create(
                        @Valid @RequestBody PlaceSubmissionCreateRequest request) {

                return ApiResponse.success(
                                "SUBMISSION_CREATE_SUCCESS",
                                "投稿成功",
                                placeSubmissionService.create(request));
        }

        @RequirePermission("SUBMISSION_ALL_LIST")
        @GetMapping
        public ApiResponse<List<PlaceSubmission>> findAll() {

                return ApiResponse.success(
                                "SUBMISSION_LIST_SUCCESS",
                                "取得投稿成功",
                                placeSubmissionService.findAll());
        }

        @RequirePermission("SUBMISSION_GET")
        @GetMapping("/{submissionId}")
        public ApiResponse<PlaceSubmission> findById(
                        @PathVariable Integer submissionId) {

                return ApiResponse.success(
                                "SUBMISSION_GET_SUCCESS",
                                "取得投稿成功",
                                placeSubmissionService.findById(submissionId));
        }

        @RequirePermission("SUBMISSION_APPROVE")
        @PutMapping("/{submissionId}/approve")
        public ApiResponse<PlaceSubmission> approve(
                        @PathVariable Integer submissionId) {

                return ApiResponse.success(
                                "SUBMISSION_APPROVE_SUCCESS",
                                "投稿審核通過",
                                placeSubmissionService.approve(submissionId));
        }

        @RequirePermission("SUBMISSION_REJECT")
        @PutMapping("/{submissionId}/reject")
        public ApiResponse<PlaceSubmission> reject(
                        @PathVariable Integer submissionId,
                        @RequestBody RejectSubmissionRequest request) {

                return ApiResponse.success(
                                "SUBMISSION_REJECT_SUCCESS",
                                "投稿已駁回",
                                placeSubmissionService.reject(
                                                submissionId,
                                                request.getAdminNote()));
        }

        @RequirePermission("SUBMISSION_REVISION")
        @PutMapping("/{submissionId}/revision")
        public ApiResponse<PlaceSubmission> requestRevision(
                        @PathVariable Integer submissionId,
                        @RequestBody RejectSubmissionRequest request) {

                return ApiResponse.success(
                                "SUBMISSION_REVISION_SUCCESS",
                                "投稿要求修改",
                                placeSubmissionService.requestRevision(
                                                submissionId,
                                                request.getAdminNote()));
        }

        @RequirePermission("SUBMISSION_DETAIL")
        @GetMapping("/{submissionId}/detail")
        public ApiResponse<SubmissionDetailResponse> getDetail(
                        @PathVariable Integer submissionId) {

                return ApiResponse.success(
                                "SUBMISSION_DETAIL_SUCCESS",
                                "取得投稿詳細資料成功",
                                placeSubmissionService.getSubmissionDetail(
                                                submissionId));
        }

        @RequirePermission("SUBMISSION_USER_LIST")
        @GetMapping("/user/{userId}")
        public ApiResponse<List<PlaceSubmission>> findByUserId(
                        @PathVariable Long userId) {

                return ApiResponse.success(
                                "SUBMISSION_USER_LIST_SUCCESS",
                                "取得使用者投稿成功",
                                placeSubmissionService.findByUserId(userId));
        }

        @RequirePermission("SUBMISSION_STATUS_LIST")
        @GetMapping("/status/{status}")
        public ApiResponse<List<PlaceSubmission>> findByStatus(
                        @PathVariable String status) {

                return ApiResponse.success(
                                "SUBMISSION_STATUS_LIST_SUCCESS",
                                "取得投稿成功",
                                placeSubmissionService.findByStatus(status));
        }

        @RequirePermission("SUBMISSION_STATISTICS")
        @GetMapping("/statistics")
        public ApiResponse<SubmissionStatisticsResponse> statistics() {

                return ApiResponse.success(
                                "SUBMISSION_STATISTICS_SUCCESS",
                                "取得投稿統計成功",
                                placeSubmissionService.getStatistics());
        }

        @RequirePermission("SUBMISSION_RESUBMIT")
        @PutMapping("/{id}/resubmit")
        public ApiResponse<Void> resubmit(
                        @PathVariable Integer id) {

                placeSubmissionService.resubmit(id);

                return ApiResponse.success(
                                "SUBMISSION_RESUBMIT_SUCCESS",
                                "重新送審成功",
                                null);
        }

        @RequirePermission("SUBMISSION_UPDATE")
        @PutMapping("/{submissionId}")
        public ApiResponse<PlaceSubmission> update(
                        @PathVariable Integer submissionId,
                        @Valid @RequestBody PlaceSubmissionCreateRequest request) {

                return ApiResponse.success(
                                "SUBMISSION_UPDATE_SUCCESS",
                                "修改投稿成功",
                                placeSubmissionService.update(
                                                submissionId,
                                                request));
        }
}
