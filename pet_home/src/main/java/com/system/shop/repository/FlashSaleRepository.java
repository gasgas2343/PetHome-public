// package com.system.member.shop.repository;

// import org.springframework.data.jpa.repository.JpaRepository;
// import org.springframework.data.jpa.repository.Query;
// import org.springframework.stereotype.Repository;
// import com.system.member.shop.entity.FlashSale;
// import java.time.LocalDateTime;
// import java.util.List;

// @Repository
// public interface FlashSaleRepository extends JpaRepository<FlashSale, Integer> {
//     @Query("SELECT f FROM FlashSale f WHERE f.startTime <= :now AND f.endTime >= :now")
//     List<FlashSale> findActive(LocalDateTime now);

//     List<FlashSale> findByProductSkuId(Integer productSkuId);
// }
package com.system.shop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.system.shop.entity.FlashSale;

import jakarta.persistence.LockModeType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface FlashSaleRepository extends JpaRepository<FlashSale, Integer> {

    @Query("SELECT f FROM FlashSale f WHERE f.startTime <= :now AND f.endTime >= :now")
    List<FlashSale> findActive(LocalDateTime now);

    List<FlashSale> findByProductSkuId(Integer productSkuId);

    // 悲觀鎖（保留備用）
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT f FROM FlashSale f WHERE f.id = :id")
    Optional<FlashSale> findByIdForUpdate(@Param("id") Integer id);

    Optional<FlashSale> findByProductSkuIdAndStartTimeBeforeAndEndTimeAfter(
        Integer productSkuId, LocalDateTime now1, LocalDateTime now2
    );

    // ✅ 新增：Conditional SQL 原子扣減（防超賣保底）
    @Modifying
    @Query("UPDATE FlashSale f SET f.saleStock = f.saleStock - :qty " +
           "WHERE f.id = :id AND f.saleStock >= :qty")
    int decrementStock(@Param("id") Integer id, @Param("qty") Integer qty);
}


