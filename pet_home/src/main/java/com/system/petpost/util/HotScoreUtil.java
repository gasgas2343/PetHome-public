package com.system.petpost.util;

// 熱門文章分數工具
public class HotScoreUtil {

    // 禁止建立物件
    private HotScoreUtil() {
    }

    // 熱門分數計算
    //
    // 按讚 × 3
    // 留言 × 2
    // 瀏覽 × 1
    public static double calculate(
            int likeCount,
            int commentCount,
            int viewCount) {

        return (likeCount * 5)
                + (commentCount * 3)
                + viewCount;
    }

}