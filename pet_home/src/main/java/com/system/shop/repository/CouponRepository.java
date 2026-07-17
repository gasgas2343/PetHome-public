package com.system.shop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.system.shop.entity.Coupon;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, Integer> {
    @Query("SELECT c FROM Coupon c WHERE c.isActive = 1 AND (c.startAt IS NULL OR c.startAt <= :now) AND (c.endAt IS NULL OR c.endAt >= :now)")
    List<Coupon> findActiveCoupons(LocalDateTime now);
}
