package com.system.petpost.dto;

import lombok.Data;

// AI審核結果 DTO
@Data
public class ReviewResultDTO {

    // 風險等級
    // LOW = 低風險
    // MEDIUM = 中風險
    // HIGH = 高風險
    private String riskLevel;

    // AI信心分數
    // 範圍：0 ~ 100
    private Double confidenceScore;

    // 偵測原因
    // 例如：
    // 傑哥
    // 家很大
    // 忠孝東路
    private String detectedReason;

    // AI審核結果
    // PASS = 通過
    // WARNING = 警告
    // BLOCKED = 封鎖
    private String aiResult;

    // 中文註解：命中敏感字次數，重複字也計算
    private Integer hitCount;

    // 中文註解：危險程度百分比，1 次 = 10%，最多 100%
    private Double riskPercent;

    // 中文註解：AI 模擬失誤率，命中字越多失誤率越高
    private Double aiErrorRate;

}