package com.system.member.service;

import com.system.member.dto.request.CreateNotificationRequest;
import com.system.member.dto.response.NotificationResponse;
import com.system.member.dto.response.UnreadNotificationsResponse;
import com.system.member.entity.UserBean;
import com.system.member.entity.UserNotificationBean;
import com.system.member.entity.UserProfilesBean;
import com.system.member.exception.BusinessException;
import com.system.member.repository.UserNotificationRepository;
import com.system.member.repository.UserProfileRepository;
import com.system.member.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserNotificationService {

    @Autowired
    private UsersRepository usersRepository;
    @Autowired
    private UserNotificationRepository userNotificationRepository;
    @Autowired
    private UserProfileRepository userProfileRepository;

    @Transactional
    public void createNotification(CreateNotificationRequest request) {
        UserNotificationBean notification = new UserNotificationBean();

        notification.setUser(usersRepository.findById(request.getUserId())
                .orElseThrow(() -> new BusinessException("USER_NOT_FOUND", "找不到使用者")));

        notification.setModuleCode(request.getModuleCode());
        notification.setNotificationType(request.getNotificationType());
        notification.setTitle(request.getTitle());
        notification.setContent(request.getContent());
        notification.setTargetType(request.getTargetType());
        notification.setTargetId(request.getTargetId());
        notification.setIsRead(false);
        notification.setReadAt(null);
        UserNotificationBean save = userNotificationRepository.save(notification);
    }

    public NotificationResponse toGetNotifications(UserNotificationBean notification){

        NotificationResponse response = new NotificationResponse();
        response.setId(notification.getId());
        response.setModuleCode(notification.getModuleCode());
        response.setNotificationType(notification.getNotificationType());
        response.setTitle(notification.getTitle());
        response.setContent(notification.getContent());
        response.setTargetType(notification.getTargetType());
        response.setTargetId(notification.getTargetId());
        response.setRead(notification.getIsRead());
        response.setReadAt(notification.getReadAt());
        response.setCreatedAt(notification.getCreatedAt());
        return response;
    }

    @Transactional(readOnly = true)
    public List<NotificationResponse> getAllNotifications(Long userId){
        return userNotificationRepository.findByUser_IdOrderByCreatedAtDesc(userId)
                .stream().map(this::toGetNotifications).toList();
    }

    @Transactional(readOnly = true)
    public UnreadNotificationsResponse unreadNotificationsCount(Long userId){
        Integer count = userNotificationRepository.countByUser_IdAndIsReadFalse(userId);

        return new UnreadNotificationsResponse(count);
    }

    @Transactional
    public void isReadNotification(Long userId, Long notificationId){
        UserNotificationBean notification = userNotificationRepository.findByUser_IdAndId(userId, notificationId)
                .orElseThrow(() -> new BusinessException("NOTIFICATIONS_NOT_FOUND", "找不到使用者通知"));

        notification.setIsRead(true);
        notification.setReadAt(LocalDateTime.now());
        userNotificationRepository.save(notification);
    }

    @Transactional
    public void isReadAllNotifications(Long userId){
        List<UserNotificationBean> notifications = userNotificationRepository.findByUser_IdAndIsReadFalse(userId);

        LocalDateTime now = LocalDateTime.now();
         for (UserNotificationBean notification : notifications) {
             notification.setIsRead(true);
             notification.setReadAt(now);
         }
         userNotificationRepository.saveAll(notifications);
    }

    @Transactional
    public void emailVerifyNotification(UserBean user){
        if(user.isEmailVerified()){
            return;
        }
        boolean unreadMessage = userNotificationRepository.existsByUser_IdAndNotificationTypeAndIsReadFalse(user.getId(), "EMAIL_NOT_VERIFIED");

        if(unreadMessage){
            return;
        }
        CreateNotificationRequest request = CreateNotificationRequest.of(
                user.getId(),
                "MEMBER",
                "EMAIL_NOT_VERIFIED",
                " Email 尚未驗證",
                "完成 Email 驗證後，即可使用完整會員功能。",
                "EMAIL_VERIFY",
                null
        );
        createNotification(request);
    }

    @Transactional
    public void userProfileNotification(UserBean user){
        UserProfilesBean profiles = userProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new BusinessException("PROFILE_NOT_FOUND", "找不到會員資料"));

        if( profiles.getPhone() != null && profiles.getBirthday() != null){
            return;
        }

        boolean unreadMessage = userNotificationRepository.existsByUser_IdAndNotificationTypeAndIsReadFalse(user.getId(), "PROFILE_INCOMPLETE");

        if(unreadMessage){
            return;
        }
        CreateNotificationRequest request = CreateNotificationRequest.of(
                user.getId(),
                "MEMBER",
                "PROFILE_INCOMPLETE",
                "建議補齊會員資料",
                "補齊會員資料與毛孩資料，讓毛起來便於提供更完整的服務體驗。",
                "MEMBER_PROFILE",
                null
        );
        createNotification(request);
    }
}
