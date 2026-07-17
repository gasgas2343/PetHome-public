package com.system.shop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.system.shop.entity.UserCoupon;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserCouponRepository extends JpaRepository<UserCoupon, Integer> {
    List<UserCoupon> findByUserId(Long userId);
    List<UserCoupon> findByUserIdAndIsUsed(Long userId, Integer isUsed);
    Optional<UserCoupon> findByUserIdAndCouponId(Long userId, Integer couponId);
    boolean existsByUserIdAndSourceKey(Long userId, String sourceKey);
}
