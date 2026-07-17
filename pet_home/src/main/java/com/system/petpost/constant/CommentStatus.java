package com.system.petpost.constant;

// 留言狀態
public class CommentStatus {

    // 正常
    public static final byte ACTIVE = 1;

    // 封鎖
    public static final byte BLOCKED = 2;

    // 刪除
    public static final byte DELETED = 3;

    private CommentStatus() {
    }
}