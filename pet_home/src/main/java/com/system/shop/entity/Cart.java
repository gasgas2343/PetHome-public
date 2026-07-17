// package com.system.member.shop.entity;


// import jakarta.persistence.*;
// import java.math.BigDecimal;
// import java.time.LocalDateTime;

// @Entity
// @Table(name = "shop_carts")
// public class Cart {

//     @Id
//     @GeneratedValue(strategy = GenerationType.IDENTITY)
//     private Integer id;

//     @Column(name = "user_id", nullable = false)
//     private Long userId;

//     @Column(name = "product_sku_id", nullable = false)
//     private Integer productSkuId;

//     @Column(name = "unit_price", nullable = false, precision = 10, scale = 2)
//     private BigDecimal unitPrice;

//     @Column(name = "quantity", nullable = false)
//     private Integer quantity = 1;

//     @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
//     private BigDecimal totalAmount;

//     @Column(name = "updated_at", nullable = false)
//     private LocalDateTime updatedAt;

//     @PrePersist
//     @PreUpdate
//     protected void onUpdate() {
//         updatedAt = LocalDateTime.now();
//         if (unitPrice != null && quantity != null) {
//             totalAmount = unitPrice.multiply(BigDecimal.valueOf(quantity));
//         }
//     }

//     // Getters and Setters
//     public Integer getId() { return id; }
//     public void setId(Integer id) { this.id = id; }

//     public Long getUserId() { return userId; }
//     public void setUserId(Long userId) { this.userId = userId; }

//     public Integer getProductSkuId() { return productSkuId; }
//     public void setProductSkuId(Integer productSkuId) { this.productSkuId = productSkuId; }

//     public BigDecimal getUnitPrice() { return unitPrice; }
//     public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }

//     public Integer getQuantity() { return quantity; }
//     public void setQuantity(Integer quantity) { this.quantity = quantity; }

//     public BigDecimal getTotalAmount() { return totalAmount; }
//     public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

//     public LocalDateTime getUpdatedAt() { return updatedAt; }
//     public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
// }
package com.system.shop.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "shop_carts")
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_id", nullable = false)
    private long userId; 

    @Column(name = "product_sku_id", nullable = false)
    private Integer productSkuId;

    @Column(name = "unit_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;

    @Column(name = "quantity", nullable = false)
    private Integer quantity = 1;

    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount = BigDecimal.ZERO; 

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.updatedAt = LocalDateTime.now();
        calculateTotal();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
        calculateTotal();
    }

    // 💡 獨立計算邏輯，預防 unitPrice 或 quantity 為 null 時引發的 Exception
    private void calculateTotal() {
        if (this.unitPrice != null && this.quantity != null) {
            this.totalAmount = this.unitPrice.multiply(BigDecimal.valueOf(this.quantity));
        } else {
            this.totalAmount = BigDecimal.ZERO;
        }
    }

    // ==================== Getters and Setters ====================

    public Integer getId() { 
        return id; 
    }
    
    public void setId(Integer id) { 
        this.id = id; 
    }

    public long getUserId() { 
        return userId; 
    }
    
    public void setUserId(long userId) { 
        this.userId = userId; 
    }

    public Integer getProductSkuId() { 
        return productSkuId; 
    }
    
    public void setProductSkuId(Integer productSkuId) { 
        this.productSkuId = productSkuId; 
    }

    public BigDecimal getUnitPrice() { 
        return unitPrice; 
    }
    
    public void setUnitPrice(BigDecimal unitPrice) { 
        this.unitPrice = unitPrice; 
    }

    public Integer getQuantity() { 
        return quantity; 
    }
    
    public void setQuantity(Integer quantity) { 
        this.quantity = quantity; 
    }

    public BigDecimal getTotalAmount() { 
        return totalAmount; 
    }
    
    public void setTotalAmount(BigDecimal totalAmount) { 
        this.totalAmount = totalAmount; 
    }

    public LocalDateTime getUpdatedAt() { 
        return updatedAt; 
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) { 
        this.updatedAt = updatedAt; 
    }
}