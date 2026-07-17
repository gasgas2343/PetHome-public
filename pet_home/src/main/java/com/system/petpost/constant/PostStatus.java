package com.system.petpost.constant;

// 文章狀態
public class PostStatus {

    // 正常
    public static final byte ACTIVE = 1;

    // 封鎖
    public static final byte BLOCKED = 2;

    // 刪除
    public static final byte DELETED = 3;

    private PostStatus() {
    }
}