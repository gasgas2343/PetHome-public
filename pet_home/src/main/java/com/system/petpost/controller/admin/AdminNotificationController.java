package com.system.petpost.controller.admin;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.system.member.entity.UserBean;
import com.system.member.repository.UsersRepository;
import com.system.member.security.annotation.RequirePermission;
import com.system.petpost.dto.admin.AdminNotificationDTO;
import com.system.petpost.entity.Notification;
import com.system.petpost.repository.NotificationRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/admin/notifications")
@RequiredArgsConstructor
public class AdminNotificationController {

    private final NotificationRepository notificationRepository;
    private final UsersRepository usersRepository;

    @GetMapping
    @RequirePermission("FORUM_ADMIN_POST_MANAGE")
    public ResponseEntity<List<AdminNotificationDTO>> findAll() {
        List<AdminNotificationDTO> result = notificationRepository
                .findByIsDeletedFalseOrderByCreatedAtDesc()
                .stream()
                .map(this::toDTO)
                .toList();

        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{notificationId}")
    @RequirePermission("FORUM_ADMIN_POST_MANAGE")
    public ResponseEntity<String> delete(@PathVariable Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("通知不存在"));

        notification.setIsDeleted(true);
        notification.setUpdatedAt(LocalDateTime.now());
        notificationRepository.save(notification);

        return ResponseEntity.ok("通知紀錄已刪除");
    }

    @DeleteMapping
    @RequirePermission("FORUM_ADMIN_POST_MANAGE")
    public ResponseEntity<String> deleteAll() {
        List<Notification> notifications = notificationRepository.findByIsDeletedFalseOrderByCreatedAtDesc();
        LocalDateTime now = LocalDateTime.now();

        for (Notification notification : notifications) {
            notification.setIsDeleted(true);
            notification.setUpdatedAt(now);
        }

        notificationRepository.saveAll(notifications);

        return ResponseEntity.ok("所有通知紀錄已刪除");
    }

    private AdminNotificationDTO toDTO(Notification notification) {
        UserBean user = usersRepository.findById(notification.getUserId()).orElse(null);

        AdminNotificationDTO dto = new AdminNotificationDTO();
        dto.setNotificationId(notification.getNotificationId());
        dto.setUserId(notification.getUserId());
        dto.setUserName(resolveUserName(user));
        dto.setUserEmail(user == null ? null : user.getEmail());
        dto.setType(notification.getType());
        dto.setMessage(notification.getMessage());
        dto.setIsRead(notification.getIsRead());
        dto.setReferenceType(notification.getReferenceType());
        dto.setReferenceId(notification.getReferenceId());
        dto.setCreatedAt(notification.getCreatedAt());

        return dto;
    }

    private String resolveUserName(UserBean user) {
        if (user == null) {
            return "未知會員";
        }

        if (user.getProfiles() != null && user.getProfiles().getNickname() != null
                && !user.getProfiles().getNickname().isBlank()) {
            return user.getProfiles().getNickname();
        }

        if (user.getProfiles() != null && user.getProfiles().getFullName() != null
                && !user.getProfiles().getFullName().isBlank()) {
            return user.getProfiles().getFullName();
        }

        return "會員 " + user.getId();
    }
}