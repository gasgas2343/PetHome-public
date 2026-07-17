package com.system.petpost.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// 日期工具類別
public class DateUtil {

    // 日期時間格式
    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern(
                    "yyyy-MM-dd HH:mm:ss");

    // 日期格式
    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern(
                    "yyyy-MM-dd");

    // 轉換日期時間字串
    public static String formatLocalDate(
            LocalDateTime dateTime) {

        if (dateTime == null) {
            return "";
        }

        return dateTime.format(
                DATE_TIME_FORMATTER);
    }

    // 轉換日期字串
    public static String formatDate(
            LocalDateTime dateTime) {

        if (dateTime == null) {
            return "";
        }

        return dateTime.format(
                DATE_FORMATTER);
    }

    // 取得目前時間
    public static LocalDateTime now() {

        return LocalDateTime.now();
    }

}