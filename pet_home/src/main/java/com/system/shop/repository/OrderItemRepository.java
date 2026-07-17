// package com.system.member.shop.repository;

// import org.springframework.data.jpa.repository.JpaRepository;
// import org.springframework.stereotype.Repository;
// import com.system.member.shop.entity.OrderItem;
// import java.util.List;

// @Repository
// public interface OrderItemRepository extends JpaRepository<OrderItem, Integer> {
//     List<OrderItem> findByOrderId(Integer orderId);
// }

package com.system.shop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.system.shop.entity.OrderItem;

import java.util.List;

@Repository
public interface OrderItemRepository
        extends JpaRepository<OrderItem, Integer> {

    List<OrderItem> findByOrderId(Integer orderId);
}
