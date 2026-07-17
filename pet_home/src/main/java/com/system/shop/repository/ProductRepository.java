package com.system.shop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.system.shop.entity.Product;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
    
    // 這些方法名稱必須對應 Entity 裡的欄位名稱 (例如 brandId, categoryId)
    List<Product> findByBrandId(Integer brandId);
    List<Product> findByCategoryId(Integer categoryId);

    // 小撇步：如果你發現刪除商品時會噴錯（因為有 SKU 關聯），
    // 未來可能需要加入 @Transactional 或是處理關聯刪除 (Cascade)
}