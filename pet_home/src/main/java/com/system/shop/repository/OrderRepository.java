// package com.system.member.shop.repository;

// import org.springframework.data.jpa.repository.JpaRepository;
// import org.springframework.stereotype.Repository;
// import com.system.member.shop.entity.Order;
// import java.util.List;
// import java.util.Optional;

// @Repository
// public interface OrderRepository extends JpaRepository<Order, Integer> {
//     List<Order> findByUserIdOrderByCreatedAtDesc(Long userId);
//     Optional<Order> findByOrderNo(String orderNo);
//     List<Order> findByOrderStatus(String orderStatus);
// }
package com.system.shop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.system.shop.entity.Order;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository
        extends JpaRepository<Order, Integer> {

    List<Order> findByUserIdOrderByCreatedAtDesc(
            Long userId
    );

    Optional<Order> findByOrderNo(
            String orderNo
    );

    List<Order> findByOrderStatus(
            String orderStatus
    );
    // 找出超時未付款的訂單
List<Order> findByPaymentStatusAndCreatedAtBefore(String paymentStatus, LocalDateTime time);
}
