package com.system.petpost.util;

import java.util.Arrays;
import java.util.List;

// 敏感字工具
public class SensitiveWordUtil {

    // 敏感字清單
    private static final List<String> SENSITIVE_WORDS = Arrays.asList(
            "忠孝東路",
            "五段RFC1149");

    // 禁止建立物件
    private SensitiveWordUtil() {
    }

    // 計算命中敏感字數量
    // 中文註解：重複出現也會計算
    // 例如：內容「忠孝東路忠孝」
    // 若敏感字有「忠孝」，會算 2 次
    public static int countSensitiveWords(String content) {

        if (content == null || content.isBlank()) {
            return 0;
        }

        int count = 0;

        for (String word : SENSITIVE_WORDS) {

            if (word == null || word.isBlank()) {
                continue;
            }

            int index = 0;

            while ((index = content.indexOf(word, index)) != -1) {

                // 中文註解：每找到一次就 +1
                count++;

                // 中文註解：往後移動，避免同一個位置重複計算
                index += word.length();
            }
        }

        return count;
    }
}