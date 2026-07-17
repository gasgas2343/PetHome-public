package com.system.shop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.system.shop.entity.OrderReturn;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderReturnRepository extends JpaRepository<OrderReturn, Integer> {
    List<OrderReturn> findByUserId(Long userId);
    List<OrderReturn> findByOrderId(Integer orderId);
    Optional<OrderReturn> findByReturnNo(String returnNo);
}
