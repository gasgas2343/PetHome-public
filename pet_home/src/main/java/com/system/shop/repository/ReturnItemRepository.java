package com.system.shop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.system.shop.entity.ReturnItem;

import java.util.List;

@Repository
public interface ReturnItemRepository extends JpaRepository<ReturnItem, Integer> {
    List<ReturnItem> findByReturnId(Integer returnId);
}
