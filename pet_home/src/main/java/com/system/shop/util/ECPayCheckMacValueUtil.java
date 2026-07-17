package com.system.shop.util;

import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.Map;
import java.util.TreeMap;

public class ECPayCheckMacValueUtil {

    /**
     * 產生綠界要求的 CheckMacValue 檢查碼
     * 規則：參數依 A-Z 排序 -> 前後加 HashKey/HashIV -> URL encode -> 轉小寫 -> SHA256 -> 轉大寫
     */
    public static String generate(Map<String, String> params, String hashKey, String hashIv) {
        // 1. 依參數名稱做字母排序
        TreeMap<String, String> sortedParams = new TreeMap<>(params);

        // 2. 組成 HashKey=xxx&參數1=值1&參數2=值2...&HashIV=xxx
        StringBuilder sb = new StringBuilder();
        sb.append("HashKey=").append(hashKey);
        for (Map.Entry<String, String> entry : sortedParams.entrySet()) {
            sb.append("&").append(entry.getKey()).append("=").append(entry.getValue());
        }
        sb.append("&HashIV=").append(hashIv);

        // 3. URL encode（綠界規定要用 .NET 的 URL encode 規則，這裡做對應轉換）
        String encoded = urlEncode(sb.toString());

        // 4. 轉小寫
        encoded = encoded.toLowerCase();

        // 5. SHA256 加密
        String sha256 = sha256(encoded);

        // 6. 轉大寫
        return sha256.toUpperCase();
    }

    private static String urlEncode(String value) {
        try {
            String encoded = URLEncoder.encode(value, "UTF-8");
            // .NET 的 URL encode 跟 Java 有些符號不同，需要手動修正
            encoded = encoded.replace("%2D", "-")
                    .replace("%5F", "_")
                    .replace("%2E", ".")
                    .replace("%21", "!")
                    .replace("%2A", "*")
                    .replace("%28", "(")
                    .replace("%29", ")");
                    
            return encoded;
        } catch (Exception e) {
            throw new RuntimeException("URL encode 失敗", e);
        }
    }

    private static String sha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes("UTF-8"));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException("SHA256 加密失敗", e);
        }
    }
}
