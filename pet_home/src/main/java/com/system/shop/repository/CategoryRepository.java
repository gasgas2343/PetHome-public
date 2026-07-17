package com.system.shop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.system.shop.entity.Category;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {
    
    // 您可以根據需求新增查詢方法
    // 例如：搜尋所有屬於特定父分類的子分類
    List<Category> findByParentId(Integer parentId);
    
    // 或者單純使用父類別提供的 findAll() 即可達成樹狀結構轉換
}
