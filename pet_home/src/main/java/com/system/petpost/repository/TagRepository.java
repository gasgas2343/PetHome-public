package com.system.petpost.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.system.petpost.entity.Tag;

public interface TagRepository
        extends JpaRepository<Tag, Long> {

    // 依標籤名稱查詢
    Optional<Tag> findByTagName(
            String tagName);

    // 模糊搜尋標籤
    List<Tag> findByTagNameContaining(
            String keyword);

    // 檢查標籤是否存在
    boolean existsByTagName(
            String tagName);

}