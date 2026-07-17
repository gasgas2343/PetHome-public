package com.system.petpost.constant;

// 檢舉狀態
public class ReportStatus {

    // 待審核
    public static final byte PENDING = 1;

    // 檢舉成立
    public static final byte APPROVED = 2;

    // 檢舉駁回
    public static final byte REJECTED = 3;

    private ReportStatus() {
    }
}