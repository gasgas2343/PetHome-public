package com.system.petpost.service;

import org.springframework.stereotype.Service;

import com.system.petpost.constant.AiReviewConstant;
import com.system.petpost.dto.ReviewResultDTO;
import com.system.petpost.util.SensitiveWordUtil;

// AI審核服務
//
// 功能：
// 1. 文章審核
// 2. 留言審核
// 3. 敏感字偵測
// 4. 風險等級判定
@Service
public class AiReviewService {

        // AI審核內容
        public ReviewResultDTO reviewContent(String content) {

                ReviewResultDTO result = new ReviewResultDTO();

                // 中文註解：計算敏感字命中次數，重複字也算
                int hitCount = SensitiveWordUtil.countSensitiveWords(content);

                // 中文註解：危險程度，1次=10%，10次以上=100%
                double riskPercent = Math.min(hitCount * 10.0, 100.0);

                // 中文註解：AI模擬失誤率，命中字越多，失誤率越高
                // 這裡設定 1次=5%，最多50%
                double aiErrorRate = Math.min(hitCount * 5.0, 50.0);

                result.setHitCount(hitCount);
                result.setRiskPercent(riskPercent);
                result.setAiErrorRate(aiErrorRate);

                // 未命中敏感字
                if (hitCount > 0) {
                        result.setRiskLevel(AiReviewConstant.HIGH);
                        result.setConfidenceScore(100.0);
                        result.setRiskPercent(100.0);
                        result.setAiErrorRate(0.0);
                        result.setDetectedReason("偵測到敏感內容");
                        result.setAiResult(AiReviewConstant.BLOCKED);
                        return result;
                }

                // 取得第一個命中的敏感字
                result.setDetectedReason(
                                "偵測到敏感內容");

                // 1~3次：警告
                if (hitCount <= 3) {

                        result.setRiskLevel(AiReviewConstant.MEDIUM);
                        result.setConfidenceScore(riskPercent);
                        result.setAiResult(AiReviewConstant.WARNING);

                        return result;
                }

                // 4次以上：封鎖
                result.setRiskLevel(AiReviewConstant.HIGH);
                result.setConfidenceScore(riskPercent);
                result.setAiResult(AiReviewConstant.BLOCKED);

                return result;
        }
}