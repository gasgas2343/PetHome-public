package com.system.shop.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.system.shop.entity.Wishlist;
import com.system.shop.repository.ProductRepository;
import com.system.shop.repository.WishlistRepository;

import java.util.List;

@Service
public class WishlistService {

    @Autowired private WishlistRepository wishlistRepository;
    @Autowired private ProductRepository  productRepository;

    public List<Wishlist> getWishlist(Long userId) {
        return wishlistRepository.findByUserId(userId);
    }

    @Transactional
    public Wishlist addToWishlist(Long userId, Integer productId) {
        productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("商品不存在: " + productId));

        if (wishlistRepository.existsByUserIdAndProductId(userId, productId)) {
            throw new RuntimeException("此商品已在收藏清單中");
        }

        Wishlist w = new Wishlist();
        w.setUserId(userId);
        w.setProductId(productId);
        return wishlistRepository.save(w);
    }

    @Transactional
    public void removeFromWishlist(Long userId, Integer productId) {
        if (!wishlistRepository.existsByUserIdAndProductId(userId, productId)) {
            throw new RuntimeException("收藏清單中無此商品");
        }
        wishlistRepository.deleteByUserIdAndProductId(userId, productId);
    }

    public boolean isWishlisted(Long userId, Integer productId) {
        return wishlistRepository.existsByUserIdAndProductId(userId, productId);
    }
}

