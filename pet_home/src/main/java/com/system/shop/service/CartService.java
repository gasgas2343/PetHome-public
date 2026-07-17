package com.system.shop.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.system.shop.dto.CartItemResponse;
import com.system.shop.entity.Cart;
import com.system.shop.entity.FlashSale;
import com.system.shop.entity.Product;
import com.system.shop.entity.ProductSku;
import com.system.shop.repository.CartRepository;
import com.system.shop.repository.FlashSaleRepository;
import com.system.shop.repository.ProductRepository;
import com.system.shop.repository.ProductSkuRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CartService {

    @Autowired private CartRepository cartRepository;
    @Autowired private ProductSkuRepository productSkuRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private FlashSaleRepository flashSaleRepository;

    public List<CartItemResponse> getCartByUser(Long userId) {
        List<Cart> carts = cartRepository.findByUserId(userId);
        return carts.stream().map(cart -> {
            ProductSku sku = productSkuRepository.findById(cart.getProductSkuId()).orElse(null);
            Product product = (sku != null)
                ? productRepository.findById(sku.getProductId()).orElse(null)
                : null;
            return new CartItemResponse(
    cart.getId(),
    cart.getProductSkuId(),
    product != null ? product.getName() : "未知商品",
    product != null ? product.getImageUrl() : "",
    cart.getUnitPrice() != null ? cart.getUnitPrice() : BigDecimal.ZERO,
    sku != null ? sku.getPrice() : BigDecimal.ZERO,  // ← 新增 originalPrice
    cart.getQuantity() != null ? cart.getQuantity() : 0,
    cart.getTotalAmount() != null ? cart.getTotalAmount() : BigDecimal.ZERO
);
        }).collect(Collectors.toList());
    }

    @Transactional
    public Cart addToCart(Long userId, Integer productSkuId, Integer quantity, BigDecimal unitPrice) {
        ProductSku sku = productSkuRepository.findById(productSkuId)
                .orElseThrow(() -> new RuntimeException("SKU 不存在"));

        int available = sku.getStock() - sku.getReservedStock();
        if (available < quantity) {
            throw new RuntimeException("庫存不足，可用數量: " + available);
        }

        Cart cart = cartRepository.findByUserIdAndProductSkuId(userId, productSkuId)
                .orElseGet(() -> {
                    Cart c = new Cart();
                    c.setUserId(userId);
                    c.setProductSkuId(productSkuId);
                    c.setQuantity(0);
                    return c;
                });

        LocalDateTime now = LocalDateTime.now();
        BigDecimal price = flashSaleRepository
                .findByProductSkuIdAndStartTimeBeforeAndEndTimeAfter(productSkuId, now, now)
                .map(FlashSale::getSalePrice)
                .orElse(unitPrice != null ? unitPrice : sku.getPrice());

        cart.setQuantity(cart.getQuantity() + quantity);
        cart.setUnitPrice(price);

        return cartRepository.save(cart);
    }

    @Transactional
    public void removeFromCart(Long userId, Integer productSkuId) {
        cartRepository.findByUserIdAndProductSkuId(userId, productSkuId)
                .ifPresent(cartRepository::delete);
    }

    @Transactional
    public void clearCart(Long userId) {
        cartRepository.deleteByUserId(userId);
    }
}