package com.system.petpost.service;

import java.util.List;

import com.system.petpost.entity.Notification;

/**
 * 通知 Service 介面
 *
 * 中文註解：
 * 通知建立、查詢、已讀狀態全部統一從這個介面呼叫。
 */
public interface NotificationService {

        Notification markAsRead(Long notificationId);

        /**
         * 中文註解：建立通知。
         */
        Notification createNotification(
                        Long userId,
                        String type,
                        Long referenceId,
                        String message);

        Notification createSystemNotification(
                        Long userId,
                        String message);

        /**
         * 中文註解：查詢指定會員所有通知。
         */
        List<Notification> findByUserId(Long userId);

        /**
         * 中文註解：查詢指定會員未讀通知。
         */
        List<Notification> findUnreadByUserId(Long userId);

        /**
         * 中文註解：查詢指定會員未讀通知數量。
         */
        Long countUnreadByUserId(Long userId);

        /**
         * 中文註解：查詢單筆通知。
         */
        Notification findById(Long notificationId, Long userId);

        /**
         * 中文註解：標記單筆通知為已讀。
         */
        Notification markAsRead(Long notificationId, Long userId);

        /**
         * 中文註解：指定會員所有通知標記為已讀。
         */
        void markAllAsRead(Long userId);

        /**
         * 中文註解：軟刪除通知。
         */
        void deleteNotification(Long notificationId, Long userId);
}
