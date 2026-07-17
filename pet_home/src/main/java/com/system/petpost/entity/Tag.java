package com.system.petpost.entity;

import jakarta.persistence.*;

import lombok.Data;

@Data
@Entity
@Table(name = "forum_tags")
public class Tag {

    // 標籤編號
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tag_id")
    private Long tagId;

    // 標籤名稱
    // 例如：柴犬、貓咪、健康、訓練
    @Column(name = "tag_name", nullable = false, unique = true, length = 50)
    private String tagName;
}
