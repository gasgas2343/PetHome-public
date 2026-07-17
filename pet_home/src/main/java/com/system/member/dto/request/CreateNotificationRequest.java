package com.system.member.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CreateNotificationRequest {
    @NotNull(message = "接收者 ID 不可為空")
    private Long userId;

    @NotBlank(message = "模組代碼不可為空")
    private String moduleCode;

    @NotBlank(message = "通知類型不可為空")
    private String notificationType;

    @NotBlank(message = "通知標題不可為空")
    private String title;

    @NotBlank(message = "通知內容不可為空")
    private String content;

    private String targetType;

    private Long targetId;

    public static CreateNotificationRequest of(
            Long userId, String moduleCode, String notificationType, String title,
            String content, String targetType, Long targetId) {
        CreateNotificationRequest request = new CreateNotificationRequest();

        request.setUserId(userId);
        request.setModuleCode(moduleCode);
        request.setNotificationType(notificationType);
        request.setTitle(title);
        request.setContent(content);
        request.setTargetType(targetType);
        request.setTargetId(targetId);

        return request;
    }
}
