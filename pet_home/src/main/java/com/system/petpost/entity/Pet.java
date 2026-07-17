package com.system.petpost.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "pet_pets")
public class Pet {
    // 毛孩編號
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pet_id")
    private Long petId;
    // 飼主會員編號
    @Column(name = "user_id", nullable = false)
    private Long userId;
    // 毛孩名稱
    @Column(name = "pet_name", nullable = false, length = 50)
    private String petName;
    // 毛孩介紹
    @Column(name = "pet_intro", length = 300)
    private String petIntro;
    // 毛孩頭像
    @Column(name = "pet_avatar_url", length = 500)
    private String petAvatarUrl;
    // 建立時間
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    // 更新時間
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}