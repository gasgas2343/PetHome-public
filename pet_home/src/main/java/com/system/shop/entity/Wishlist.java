package com.system.shop.entity;



import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "shop_wishlists")
@IdClass(Wishlist.WishlistId.class)
public class Wishlist {

    @Id
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Id
    @Column(name = "product_id", nullable = false)
    private Integer productId;

    // Composite PK class
    public static class WishlistId implements Serializable {
        private Long userId;
        private Integer productId;

        public WishlistId() {}
        public WishlistId(Long userId, Integer productId) {
            this.userId = userId;
            this.productId = productId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof WishlistId)) return false;
            WishlistId that = (WishlistId) o;
            return Objects.equals(userId, that.userId) && Objects.equals(productId, that.productId);
        }

        @Override
        public int hashCode() { return Objects.hash(userId, productId); }
    }

    // Getters and Setters
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Integer getProductId() { return productId; }
    public void setProductId(Integer productId) { this.productId = productId; }
}
