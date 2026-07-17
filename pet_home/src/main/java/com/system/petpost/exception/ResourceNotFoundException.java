package com.system.petpost.exception;

/**
 * 查無資料例外
 *
 * 用途：
 * 文章不存在
 * 留言不存在
 * 時間軸不存在
 * 商品不存在
 * 通知不存在
 */
public class ResourceNotFoundException
        extends RuntimeException {

    /**
     * 建構子
     */
    public ResourceNotFoundException(
            String message) {

        super(message);

    }

}