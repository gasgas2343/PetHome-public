package com.system.petpost.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Data;

/**
 * 寵物時間軸貼文 Entity
 *
 * 一筆資料代表時間軸上的一個節點。
 */
@Data
@Entity
@Table(name = "pet_pet_posts")
public class PetPost {

    // 時間軸貼文編號
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pet_post_id")
    private Long petPostId;

    // 毛孩編號
    @Column(name = "pet_id", nullable = false)
    private Long petId;


    // 貼文類型：DIARY / BIRTHDAY / VACCINE / MEDICAL / EVENT
    @Column(name = "post_type", nullable = false, length = 30)
    private String postType = "DIARY";

    // 時間軸標題
    @Column(name = "title", nullable = false, length = 100)
    private String title;

    // 時間軸內容
    @Column(name = "content", length = 1000)
    private String content;

    // 時間軸日期
    @Column(name = "event_date", nullable = false)
    private LocalDate postDate;

    // Cloudinary 圖片網址
    @Column(name = "image_url", length = 500)
    private String imageUrl;

    // 狀態：1=公開，2=隱藏
    @Column(name = "status")
    private Byte status = 1;

    // 建立時間
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // 更新時間
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}