package com.system.shop.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.system.shop.dto.CouponResponse;
import com.system.shop.entity.Coupon;
import com.system.shop.entity.UserCoupon;
import com.system.shop.repository.CouponRepository;
import com.system.shop.repository.UserCouponRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class CouponService {

    @Autowired private CouponRepository couponRepository;
    @Autowired private UserCouponRepository userCouponRepository;

    // public List<UserCoupon> getUserCoupons(Long userId) {
    //     return userCouponRepository.findByUserIdAndIsUsed(userId, 0);
    // }

    /**
     * 計算折扣金額
     */
    public BigDecimal calculateDiscountAmount(Long userId, Integer userCouponId, BigDecimal orderTotal) {
        UserCoupon userCoupon = userCouponRepository.findById(userCouponId)
                .orElseThrow(() -> new RuntimeException("優惠券不存在"));

        if (!userCoupon.getUserId().equals(userId)) {
            throw new RuntimeException("此優惠券不屬於該使用者");
        }
        if (userCoupon.getIsUsed() == 1) {
            throw new RuntimeException("此優惠券已使用");
        }
        if (userCoupon.getExpireAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("此優惠券已過期");
        }

        Coupon coupon = couponRepository.findById(userCoupon.getCouponId())
                .orElseThrow(() -> new RuntimeException("優惠券資料不存在"));

        if (coupon.getMinSpend() != null && orderTotal.compareTo(coupon.getMinSpend()) < 0) {
            throw new RuntimeException("未達最低消費門檻: " + coupon.getMinSpend());
        }

        if ("FIXED".equals(coupon.getDiscountType())) {
            return coupon.getDiscountValue().min(orderTotal);
        } else if ("PERCENT".equals(coupon.getDiscountType())) {
            return orderTotal.multiply(coupon.getDiscountValue())
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        }
        return BigDecimal.ZERO;
    }

    @Transactional
    public void markAsUsed(Long userId, Integer userCouponId) {
        UserCoupon uc = userCouponRepository.findById(userCouponId)
                .orElseThrow(() -> new RuntimeException("優惠券不存在"));
        uc.setIsUsed(1);
        uc.setUsedAt(LocalDateTime.now());
        userCouponRepository.save(uc);
    }

    @Transactional
    public UserCoupon issueCoupon(Long userId, Integer couponId, String sourceType, String sourceKey, Integer validDays) {
        if (userCouponRepository.existsByUserIdAndSourceKey(userId, sourceKey)) {
            throw new RuntimeException("已領取過此優惠券");
        }
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new RuntimeException("優惠券不存在"));

        UserCoupon uc = new UserCoupon();
        uc.setUserId(userId);
        uc.setCouponId(couponId);
        uc.setSourceType(sourceType);
        uc.setSourceKey(sourceKey);
        uc.setIssuedAt(LocalDateTime.now());

        int days = (validDays != null) ? validDays :
                   (coupon.getValidDays() != null ? coupon.getValidDays() : 30);
        uc.setExpireAt(LocalDateTime.now().plusDays(days));

        return userCouponRepository.save(uc);
    }
    public List<CouponResponse> getUserCoupons(Long userId) {
    List<UserCoupon> userCoupons = userCouponRepository.findByUserIdAndIsUsed(userId, 0);
    
    return userCoupons.stream().map(uc -> {
        Coupon coupon = couponRepository.findById(uc.getCouponId()).orElse(null);
        if (coupon == null) return null;
        // 借用 CouponResponse，額外帶 userCouponId
        return CouponResponse.fromWithUserCouponId(coupon, uc.getId());
    }).filter(dto -> dto != null).collect(java.util.stream.Collectors.toList());
}
}
