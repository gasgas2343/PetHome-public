package com.system.petpost.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.system.member.exception.BusinessException;
import com.system.petpost.constant.NotificationType;
import com.system.petpost.entity.Notification;
import com.system.petpost.repository.NotificationRepository;
import com.system.petpost.service.NotificationService;

import lombok.RequiredArgsConstructor;

/**
 * 通知 Service 實作
 *
 * 中文註解：
 * 只保留 createNotification() 與 createSystemNotification() 兩個建立通知入口。
 * Report / Appeal / Comment / Like 等功能都透過 createNotification() 建立通知。
 * SYSTEM 系統通知因為 referenceId 可以是 null，所以另外使用 createSystemNotification()。
 */
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    // 中文註解：通知 Repository，負責 forum_notifications 資料表。
    private final NotificationRepository notificationRepository;

    @Override
    public Notification markAsRead(Long notificationId) {

        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new BusinessException(
                        "NOTIFICATION_NOT_FOUND",
                        "通知不存在"));

        notification.setIsRead(true);

        return notificationRepository.save(notification);
    }

    /**
     * 中文註解：建立一般通知。
     */
    @Override
    public Notification createNotification(
            Long userId,
            String type,
            Long referenceId,
            String message) {

        validateCreateNotification(userId, type, referenceId, message);

        LocalDateTime now = LocalDateTime.now();

        Notification notification = new Notification();

        notification.setUserId(userId);
        notification.setType(type);
        notification.setReferenceId(referenceId);
        notification.setMessage(message);
        notification.setNotificationCount(1);
        notification.setIsRead(false);
        notification.setIsDeleted(false);
        notification.setCreatedAt(now);
        notification.setUpdatedAt(now);

        return notificationRepository.save(notification);
    }

    /**
     * 中文註解：建立系統通知。
     * 系統通知沒有特定文章、留言、檢舉或申訴，因此 referenceId 可以是 null。
     */
    @Override
    public Notification createSystemNotification(Long userId, String message) {

        if (userId == null) {
            throw new BusinessException("USER_ID_REQUIRED", "會員ID不可為空");
        }

        if (message == null || message.trim().isEmpty()) {
            throw new BusinessException("NOTIFICATION_MESSAGE_REQUIRED", "系統通知內容不可為空");
        }

        LocalDateTime now = LocalDateTime.now();

        Notification notification = new Notification();

        notification.setUserId(userId);
        notification.setSenderId(null);
        notification.setType(NotificationType.SYSTEM);
        notification.setReferenceType("SYSTEM");
        notification.setReferenceId(null);
        notification.setMessage(message);
        notification.setTargetUrl("/notifications");
        notification.setNotificationCount(1);
        notification.setIsRead(false);
        notification.setIsDeleted(false);
        notification.setCreatedAt(now);
        notification.setUpdatedAt(now);

        return notificationRepository.save(notification);
    }

    /**
     * 中文註解：查詢指定會員所有通知。
     */
    @Override
    public List<Notification> findByUserId(Long userId) {

        if (userId == null) {
            throw new BusinessException(
                    "USER_ID_REQUIRED",
                    "會員ID不可為空");
        }

        return notificationRepository
                .findByUserIdAndIsDeletedFalseOrderByCreatedAtDesc(userId);
    }

    /**
     * 中文註解：查詢指定會員未讀通知。
     */
    @Override
    public List<Notification> findUnreadByUserId(Long userId) {

        if (userId == null) {
            throw new BusinessException(
                    "USER_ID_REQUIRED",
                    "會員ID不可為空");
        }

        return notificationRepository
                .findByUserIdAndIsReadFalseAndIsDeletedFalse(userId);
    }

    /**
     * 中文註解：查詢單筆通知。
     */
    @Override
    public Notification findById(Long notificationId, Long userId) {

        if (notificationId == null) {
            throw new BusinessException(
                    "NOTIFICATION_ID_REQUIRED",
                    "通知ID不可為空");
        }

        if (userId == null) {
            throw new BusinessException(
                    "USER_ID_REQUIRED",
                    "會員ID不可為空");
        }

        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new BusinessException(
                        "NOTIFICATION_NOT_FOUND",
                        "通知不存在"));

        if (Boolean.TRUE.equals(notification.getIsDeleted())) {
            throw new BusinessException(
                    "NOTIFICATION_NOT_FOUND",
                    "通知不存在");
        }

        if (!notification.getUserId().equals(userId)) {
            throw new BusinessException(
                    "NOTIFICATION_OWNER_REQUIRED",
                    "只能操作自己的通知");
        }

        return notification;
    }

    /**
     * 中文註解：標記單筆通知為已讀。
     */
    @Override
    public Notification markAsRead(Long notificationId, Long userId) {

        Notification notification = findById(notificationId, userId);

        notification.setIsRead(true);
        notification.setUpdatedAt(LocalDateTime.now());

        return notificationRepository.save(notification);
    }

    /**
     * 中文註解：指定會員所有通知標記為已讀。
     */
    @Override
    public void markAllAsRead(Long userId) {

        if (userId == null) {
            throw new BusinessException(
                    "USER_ID_REQUIRED",
                    "會員ID不可為空");
        }

        List<Notification> notifications = notificationRepository
                .findByUserIdAndIsDeletedFalseOrderByCreatedAtDesc(userId);

        if (notifications.isEmpty()) {
            return;
        }

        LocalDateTime now = LocalDateTime.now();

        for (Notification notification : notifications) {
            notification.setIsRead(true);
            notification.setUpdatedAt(now);
        }

        notificationRepository.saveAll(notifications);
    }

    /**
     * 中文註解：建立一般通知前的欄位驗證。
     */
    private void validateCreateNotification(
            Long userId,
            String type,
            Long referenceId,
            String message) {

        if (userId == null) {
            throw new BusinessException(
                    "USER_ID_REQUIRED",
                    "通知會員ID不可為空");
        }

        if (type == null || type.trim().isEmpty()) {
            throw new BusinessException(
                    "NOTIFICATION_TYPE_REQUIRED",
                    "通知類型不可為空");
        }

        if (referenceId == null) {
            throw new BusinessException(
                    "REFERENCE_ID_REQUIRED",
                    "通知參考ID不可為空");
        }

        if (message == null || message.trim().isEmpty()) {
            throw new BusinessException(
                    "NOTIFICATION_MESSAGE_REQUIRED",
                    "通知內容不可為空");
        }
    }

    /**
     * 中文註解：查詢指定會員未讀通知數量。
     */
    @Override
    public Long countUnreadByUserId(Long userId) {

        return notificationRepository
                .countByUserIdAndIsReadFalseAndIsDeletedFalse(userId);
    }

    /**
     * 中文註解：軟刪除通知。
     */
    @Override
    public void deleteNotification(Long notificationId, Long userId) {

        Notification notification = findById(notificationId, userId);

        notification.setIsDeleted(true);
        notification.setUpdatedAt(LocalDateTime.now());

        notificationRepository.save(notification);
    }
}