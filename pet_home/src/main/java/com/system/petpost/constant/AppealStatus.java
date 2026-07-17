package com.system.petpost.constant;

// 申訴狀態
public class AppealStatus {

    // 待審核
    public static final byte PENDING = 1;

    // 申訴成功
    public static final byte APPROVED = 2;

    // 申訴駁回
    public static final byte REJECTED = 3;

    private AppealStatus() {
    }
}