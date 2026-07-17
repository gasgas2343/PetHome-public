package com.system.member.repository;

import com.system.member.entity.UserNotificationBean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserNotificationRepository extends JpaRepository<UserNotificationBean, Long> {
    List<UserNotificationBean> findByUser_IdOrderByCreatedAtDesc(Long userId);

    Optional<UserNotificationBean> findByUser_IdAndId(Long userId, Long notificationId);

    Integer countByUser_IdAndIsReadFalse(Long userId);

    List<UserNotificationBean> findByUser_IdAndIsReadFalse(Long userId);

    boolean existsByUser_IdAndNotificationTypeAndIsReadFalse(Long userId, String type);

}
