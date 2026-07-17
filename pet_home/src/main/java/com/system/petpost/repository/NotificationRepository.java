package com.system.petpost.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.system.petpost.entity.Notification;

public interface NotificationRepository
        extends JpaRepository<Notification, Long> {

    // 查詢會員所有通知
List<Notification> findByUserIdAndIsDeletedFalseOrderByCreatedAtDesc(Long userId);

List<Notification> findByUserIdAndIsReadFalseAndIsDeletedFalse(Long userId);

long countByUserIdAndIsReadFalseAndIsDeletedFalse(Long userId);

    // 中文註解：論壇後台查詢全站通知。
    List<Notification> findByIsDeletedFalseOrderByCreatedAtDesc();

}