package com.system.petpost.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.system.member.dto.ApiResponse;
import com.system.member.security.annotation.RequirePermission;
import com.system.petpost.entity.Notification;
import com.system.petpost.service.NotificationService;
import com.system.petpost.util.LoginUserUtil;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

        private final NotificationService notificationService;

        /**
         * 中文註解：查詢目前登入會員所有通知。
         * userId 不再由前端傳入，改從 JWT 取得。
         */
        @GetMapping("/me")
        @RequirePermission("FORUM_NOTIFICATION_READ")
        public ResponseEntity<ApiResponse<List<Notification>>> findMyNotifications() {

                Long userId = LoginUserUtil.getLoginUserId();

                List<Notification> notifications = notificationService.findByUserId(userId);

                return ResponseEntity.ok(
                                ApiResponse.success(
                                                "NOTIFICATION_LIST_SUCCESS",
                                                "查詢通知成功",
                                                notifications));
        }

        /**
         * 中文註解：查詢目前登入會員未讀通知數量。
         */
        @GetMapping("/me/unread-count")
        @RequirePermission("FORUM_NOTIFICATION_READ")
        public ResponseEntity<ApiResponse<Long>> countMyUnreadNotifications() {

                Long userId = LoginUserUtil.getLoginUserId();

                Long count = notificationService.countUnreadByUserId(userId);

                return ResponseEntity.ok(
                                ApiResponse.success(
                                                "NOTIFICATION_UNREAD_COUNT_SUCCESS",
                                                "查詢未讀通知數量成功",
                                                count));
        }

        /**
         * 中文註解：查詢單筆通知。
         * 注意：Service 仍需檢查這筆通知是否屬於目前登入者。
         */
        @GetMapping("/{notificationId}")
        @RequirePermission("FORUM_NOTIFICATION_READ")
        public ResponseEntity<ApiResponse<Notification>> findById(
                        @PathVariable Long notificationId) {

                Long userId = LoginUserUtil.getLoginUserId();

                Notification notification = notificationService.findById(notificationId, userId);

                return ResponseEntity.ok(
                                ApiResponse.success(
                                                "NOTIFICATION_DETAIL_SUCCESS",
                                                "查詢通知成功",
                                                notification));
        }

        /**
         * 中文註解：標記單筆通知為已讀。
         * 注意：Service 仍需檢查這筆通知是否屬於目前登入者。
         */
        @PutMapping("/{notificationId}/read")
        @RequirePermission("FORUM_NOTIFICATION_READ")
        public ResponseEntity<ApiResponse<Notification>> markAsRead(
                        @PathVariable Long notificationId) {

                Long userId = LoginUserUtil.getLoginUserId();

                Notification notification = notificationService.markAsRead(notificationId, userId);

                return ResponseEntity.ok(
                                ApiResponse.success(
                                                "FORUM_NOTIFICATION_READ",
                                                "通知已讀",
                                                notification));
        }

        /**
         * 中文註解：目前登入會員全部通知標記已讀。
         */
        @PutMapping("/me/read-all")
        @RequirePermission("FORUM_NOTIFICATION_READ")
        public ResponseEntity<ApiResponse<Void>> markAllMyNotificationsAsRead() {

                Long userId = LoginUserUtil.getLoginUserId();

                notificationService.markAllAsRead(userId);

                return ResponseEntity.ok(
                                ApiResponse.success(
                                                "FORUM_NOTIFICATION_READ_ALL",
                                                "全部通知已讀",
                                                null));
        }

        /**
         * 中文註解：刪除通知。
         * 注意：Service 仍需檢查這筆通知是否屬於目前登入者。
         */
        @DeleteMapping("/{notificationId}")
        @RequirePermission("FORUM_NOTIFICATION_READ")
        public ResponseEntity<ApiResponse<Void>> deleteNotification(
                        @PathVariable Long notificationId) {

                Long userId = LoginUserUtil.getLoginUserId();

                notificationService.deleteNotification(notificationId, userId);

                return ResponseEntity.ok(
                                ApiResponse.success(
                                                "NOTIFICATION_DELETE_SUCCESS",
                                                "通知已刪除",
                                                null));
        }
}