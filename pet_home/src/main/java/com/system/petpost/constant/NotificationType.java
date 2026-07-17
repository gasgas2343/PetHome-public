package com.system.petpost.constant;

// 中文註解：通知類型常數，必須和前端 NotificationItem.vue 對齊。
public class NotificationType {

    public static final String POST_LIKE = "POST_LIKE";
    public static final String POST_COMMENT = "POST_COMMENT";
    public static final String POST_FAVORITE = "POST_FAVORITE";
    public static final String POST_SHARE = "POST_SHARE";
    public static final String COMMENT_REPLY = "COMMENT_REPLY";
    public static final String COMMENT_LIKE = "COMMENT_LIKE";
    public static final String REPORT_RESULT = "REPORT_RESULT";
    public static final String APPEAL_RESULT = "APPEAL_RESULT";
    public static final String SYSTEM = "SYSTEM";

    private NotificationType() {
    }
}