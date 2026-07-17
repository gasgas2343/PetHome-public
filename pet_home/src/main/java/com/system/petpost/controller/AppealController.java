package com.system.petpost.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.system.member.dto.ApiResponse;
import com.system.member.security.annotation.RequirePermission;
import com.system.petpost.dto.AppealCreateDTO;
import com.system.petpost.dto.admin.AdminAppealDTO;
import com.system.petpost.entity.Appeal;
import com.system.petpost.service.AppealService;
import com.system.petpost.util.LoginUserUtil;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/appeals")
@RequiredArgsConstructor
public class AppealController {

        private final AppealService appealService;

        /**
         * 中文註解：建立申訴。
         * userId 不再由前端傳入，改從 JWT 取得目前登入者。
         */
        @PostMapping
        @RequirePermission("FORUM_APPEAL_CREATE")
        public ResponseEntity<ApiResponse<Appeal>> create(
                        @RequestBody AppealCreateDTO dto) {

                Long userId = LoginUserUtil.getLoginUserId();

                dto.setUserId(userId);

                Appeal appeal = appealService.create(dto);

                return ResponseEntity.ok(
                                ApiResponse.success(
                                                "APPEAL_CREATED",
                                                "申訴送出成功",
                                                appeal));
        }

        /**
         * 中文註解：查詢所有申訴。
         * 管理員/員工權限。
         */
        @GetMapping
        @RequirePermission("FORUM_APPEAL_REVIEW")
        public ResponseEntity<ApiResponse<List<Appeal>>> findAll() {

                List<Appeal> appeals = appealService.findAll();

                return ResponseEntity.ok(
                                ApiResponse.success(
                                                "APPEAL_LIST_SUCCESS",
                                                "查詢申訴成功",
                                                appeals));
        }

        /**
         * 中文註解：查詢單筆申訴。
         * 管理員/員工權限。
         */
        @GetMapping("/{appealId}")
        @RequirePermission("FORUM_APPEAL_REVIEW")
        public ResponseEntity<ApiResponse<Appeal>> findById(
                        @PathVariable Long appealId) {

                Appeal appeal = appealService.findById(appealId);

                return ResponseEntity.ok(
                                ApiResponse.success(
                                                "APPEAL_DETAIL_SUCCESS",
                                                "查詢申訴成功",
                                                appeal));
        }

        /**
         * 中文註解：查詢待審核申訴。
         * 管理員/員工權限。
         */
        @GetMapping("/pending")
        @RequirePermission("FORUM_APPEAL_REVIEW")
        public ResponseEntity<ApiResponse<List<AdminAppealDTO>>> findPendingAppeals() {

                List<AdminAppealDTO> appeals = appealService.findPendingAppealDTOs();

                return ResponseEntity.ok(
                                ApiResponse.success(
                                                "APPEAL_PENDING_SUCCESS",
                                                "查詢待審核申訴成功",
                                                appeals));
        }

        /**
         * 中文註解：申訴通過。
         * adminId 不再由前端傳入，改從 JWT 取得目前登入管理員/員工。
         */
        @PutMapping("/{appealId}/approve")
        @RequirePermission("FORUM_APPEAL_REVIEW")
        public ResponseEntity<ApiResponse<Appeal>> approve(
                        @PathVariable Long appealId,
                        @RequestParam String note) {

                Long adminId = LoginUserUtil.getLoginUserId();

                Appeal appeal = appealService.approve(appealId, adminId, note);

                return ResponseEntity.ok(
                                ApiResponse.success(
                                                "APPEAL_APPROVED",
                                                "申訴通過",
                                                appeal));
        }

        /**
         * 中文註解：申訴駁回。
         * adminId 不再由前端傳入，改從 JWT 取得目前登入管理員/員工。
         */
        @PutMapping("/{appealId}/reject")
        @RequirePermission("FORUM_APPEAL_REVIEW")
        public ResponseEntity<ApiResponse<Appeal>> reject(
                        @PathVariable Long appealId,
                        @RequestParam String note) {

                Long adminId = LoginUserUtil.getLoginUserId();

                Appeal appeal = appealService.reject(appealId, adminId, note);

                return ResponseEntity.ok(
                                ApiResponse.success(
                                                "APPEAL_REJECTED",
                                                "申訴已駁回",
                                                appeal));
        }
}