package com.system.petpost.constant;

// 會員狀態
public class UserStatus {

    // 正常
    public static final byte ACTIVE = 1;

    // 停權
    public static final byte SUSPENDED = 2;

    // 封鎖
    public static final byte BANNED = 3;

    private UserStatus() {
    }
}
