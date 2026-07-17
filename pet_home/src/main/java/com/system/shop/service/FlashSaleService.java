// package com.system.member.shop.service;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Service;
// import org.springframework.transaction.annotation.Transactional;
// import com.system.member.shop.entity.FlashSale;
// import com.system.member.shop.entity.ProductSku;
// import com.system.member.shop.repository.FlashSaleRepository;
// import com.system.member.shop.repository.ProductSkuRepository;

// import java.math.BigDecimal;
// import java.time.LocalDateTime;
// import java.util.List;

// @Service
// public class FlashSaleService {

//     @Autowired private FlashSaleRepository flashSaleRepository;
//     @Autowired private ProductSkuRepository productSkuRepository;

//     public List<FlashSale> getActiveFlashSales() {
//         return flashSaleRepository.findActive(LocalDateTime.now());
//     }

//     public List<FlashSale> getFlashSalesBySkuId(Integer productSkuId) {
//         return flashSaleRepository.findByProductSkuId(productSkuId);
//     }

//     @Transactional
//     public FlashSale createFlashSale(Integer productSkuId, BigDecimal salePrice, Integer saleStock,
//                                      LocalDateTime startTime, LocalDateTime endTime) {
//         ProductSku sku = productSkuRepository.findById(productSkuId)
//                 .orElseThrow(() -> new RuntimeException("SKU 不存在"));

//         if (salePrice.compareTo(sku.getPrice()) >= 0) throw new RuntimeException("閃購價必須低於原價");
//         if (saleStock > sku.getStock()) throw new RuntimeException("庫存不足");

//         FlashSale fs = new FlashSale();
//         fs.setProductSkuId(productSkuId);
//         fs.setSalePrice(salePrice);
//         fs.setSaleStock(saleStock);
//         fs.setStartTime(startTime);
//         fs.setEndTime(endTime);
//         return flashSaleRepository.save(fs);
//     }

//     @Transactional
//     public FlashSale updateFlashSale(FlashSale flashSale) {
//         FlashSale exist = flashSaleRepository.findById(flashSale.getId())
//                 .orElseThrow(() -> new RuntimeException("活動不存在"));
        
//         exist.setSalePrice(flashSale.getSalePrice());
//         exist.setSaleStock(flashSale.getSaleStock());
//         exist.setStartTime(flashSale.getStartTime());
//         exist.setEndTime(flashSale.getEndTime());
//         return flashSaleRepository.save(exist);
//     }

//     @Transactional
//     public void deleteFlashSale(Integer id) {
//         flashSaleRepository.deleteById(id);
//     }

//     @Transactional
//     public BigDecimal grabFlashSale(Integer flashSaleId, Integer quantity) {
//         // 使用 Repository 的 FOR UPDATE 鎖定行資料，防止超賣
//         FlashSale fs = flashSaleRepository.findByIdForUpdate(flashSaleId)
//                 .orElseThrow(() -> new RuntimeException("活動不存在"));

//         if (LocalDateTime.now().isBefore(fs.getStartTime())) throw new RuntimeException("活動尚未開始");
//         if (LocalDateTime.now().isAfter(fs.getEndTime())) throw new RuntimeException("活動已結束");
//         if (fs.getSaleStock() < quantity) throw new RuntimeException("庫存不足");

//         fs.setSaleStock(fs.getSaleStock() - quantity);
//         flashSaleRepository.save(fs);

//         ProductSku sku = productSkuRepository.findByIdForUpdate(fs.getProductSkuId())
//                 .orElseThrow(() -> new RuntimeException("SKU 不存在"));
//         sku.setStock(sku.getStock() - quantity);
//         productSkuRepository.save(sku);

//         return fs.getSalePrice();
//     }
//     public List<FlashSale> getAllFlashSales() {
//     return flashSaleRepository.findAll();
// }
// }

// package com.system.member.shop.service;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Service;
// import org.springframework.transaction.annotation.Transactional;
// import com.system.member.shop.dto.FlashSaleDTO;
// import com.system.member.shop.entity.FlashSale;
// import com.system.member.shop.entity.Product;
// import com.system.member.shop.entity.ProductSku;
// import com.system.member.shop.repository.FlashSaleRepository;
// import com.system.member.shop.repository.ProductRepository;
// import com.system.member.shop.repository.ProductSkuRepository;

// import java.math.BigDecimal;
// import java.time.LocalDateTime;
// import java.time.format.DateTimeFormatter;
// import java.util.List;
// import java.util.stream.Collectors;

// @Service
// public class FlashSaleService {

//     @Autowired private FlashSaleRepository flashSaleRepository;
//     @Autowired private ProductSkuRepository productSkuRepository;
//     @Autowired private ProductRepository productRepository;

//     private static final DateTimeFormatter FMT = 
//         DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

//     // ✅ 改成回傳 DTO，附帶商品資訊
//     public List<FlashSaleDTO> getActiveFlashSales() {
//         LocalDateTime now = LocalDateTime.now();
//         List<FlashSale> active = flashSaleRepository.findActive(now);

//         return active.stream().map(sale -> {
//             FlashSaleDTO dto = new FlashSaleDTO();
//             dto.setFlashSaleId(sale.getId());
//             dto.setSalePrice(sale.getSalePrice());
//             dto.setSaleStock(sale.getSaleStock());
//             dto.setStartTime(sale.getStartTime().format(FMT));
//             dto.setEndTime(sale.getEndTime().format(FMT));

//             // 透過 skuId 找 SKU → 再找 Product
//             productSkuRepository.findById(sale.getProductSkuId()).ifPresent(sku -> {
//                 dto.setOriginalPrice(sku.getPrice());
//                 productRepository.findById(sku.getProductId()).ifPresent(product -> {
//                     dto.setProductId(product.getId());
//                     dto.setProductName(product.getName());
//                     dto.setImageUrl(product.getImageUrl());
//                 });
//             });

//             return dto;
//         }).collect(Collectors.toList());
//     }

//     // 其他方法維持不變
//     public List<FlashSale> getAllFlashSales() {
//         return flashSaleRepository.findAll();
//     }

//     public FlashSale createFlashSale(Integer productSkuId, BigDecimal salePrice,
//                                      Integer saleStock, LocalDateTime startTime,
//                                      LocalDateTime endTime) {
//         FlashSale fs = new FlashSale();
//         fs.setProductSkuId(productSkuId);
//         fs.setSalePrice(salePrice);
//         fs.setSaleStock(saleStock);
//         fs.setStartTime(startTime);
//         fs.setEndTime(endTime);
//         return flashSaleRepository.save(fs);
//     }

//     public FlashSale updateFlashSale(FlashSale flashSale) {
//         return flashSaleRepository.save(flashSale);
//     }

//     public void deleteFlashSale(Integer id) {
//         flashSaleRepository.deleteById(id);
//     }

//     @Transactional
//     public BigDecimal grabFlashSale(Integer flashSaleId, Integer quantity) {
//         FlashSale fs = flashSaleRepository.findById(flashSaleId)
//             .orElseThrow(() -> new RuntimeException("活動不存在"));
//         if (fs.getSaleStock() < quantity)
//             throw new RuntimeException("庫存不足");
//         fs.setSaleStock(fs.getSaleStock() - quantity);
//         flashSaleRepository.save(fs);
//         return fs.getSalePrice();
//     }
// }
package com.system.shop.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.system.shop.dto.FlashSaleDTO;
import com.system.shop.entity.FlashSale;
import com.system.shop.repository.FlashSaleRepository;
import com.system.shop.repository.ProductRepository;
import com.system.shop.repository.ProductSkuRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class FlashSaleService {

    @Autowired private FlashSaleRepository flashSaleRepository;
    @Autowired private ProductSkuRepository productSkuRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private StringRedisTemplate redisTemplate;  // ← 新增

    private static final DateTimeFormatter FMT =
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static final String STOCK_KEY = "flash:stock:";  // ← 移到這裡統一管理

    /**
     * 獲取目前進行中的閃購活動（附帶商品與規格資訊）
     */
    public List<FlashSaleDTO> getActiveFlashSales() {
        LocalDateTime now = LocalDateTime.now();
        List<FlashSale> active = flashSaleRepository.findActive(now);

        return active.stream().map(sale -> {
            FlashSaleDTO dto = new FlashSaleDTO();
            dto.setFlashSaleId(sale.getId());
            dto.setSalePrice(sale.getSalePrice());
            dto.setSaleStock(sale.getSaleStock());
            dto.setStartTime(sale.getStartTime().format(FMT));
            dto.setEndTime(sale.getEndTime().format(FMT));

            productSkuRepository.findById(sale.getProductSkuId()).ifPresent(sku -> {
                dto.setOriginalPrice(sku.getPrice());
                productRepository.findById(sku.getProductId()).ifPresent(product -> {
                    dto.setProductId(product.getId());
                    dto.setProductName(product.getName());
                    dto.setImageUrl(product.getImageUrl());
                });
            });

            return dto;
        }).collect(Collectors.toList());
    }

    /**
     * 獲取所有特賣活動清單（後台管理使用）
     */
    public List<FlashSale> getAllFlashSales() {
        return flashSaleRepository.findAll();
    }

    /**
     * 建立新閃購活動（支援多筆活動，加入時間重疊防呆）
     */
    @Transactional
    public FlashSale createFlashSale(Integer productSkuId, BigDecimal salePrice,
                                     Integer saleStock, LocalDateTime startTime,
                                     LocalDateTime endTime) {

        if (startTime.isAfter(endTime)) {
            throw new RuntimeException("開始時間不能晚於結束時間");
        }

        List<FlashSale> existingSales = flashSaleRepository.findByProductSkuId(productSkuId);
        for (FlashSale sale : existingSales) {
            if (startTime.isBefore(sale.getEndTime()) && endTime.isAfter(sale.getStartTime())) {
                throw new RuntimeException("建立失敗：該商品規格在當前輸入的時間區間內，已存在其他特賣活動！");
            }
        }

        FlashSale fs = new FlashSale();
        fs.setProductSkuId(productSkuId);
        fs.setSalePrice(salePrice);
        fs.setSaleStock(saleStock);
        fs.setStartTime(startTime);
        fs.setEndTime(endTime);
        FlashSale saved = flashSaleRepository.save(fs);

        // 建立活動時同步預載庫存到 Redis
        initStockToRedis(saved.getId(), saleStock);

        return saved;
    }

    /**
     * 預載庫存到 Redis
     */
    public void initStockToRedis(Integer flashSaleId, Integer stock) {
        String key = STOCK_KEY + flashSaleId;
        redisTemplate.opsForValue().set(key, String.valueOf(stock), 24, TimeUnit.HOURS);
        System.out.println("✅ Flash Sale " + flashSaleId + " 庫存已載入 Redis：" + stock);
    }

    /**
     * 更新閃購活動
     */
    @Transactional
    public FlashSale updateFlashSale(FlashSale flashSale) {
        if (!flashSaleRepository.existsById(flashSale.getId())) {
            throw new RuntimeException("該特賣活動不存在，無法更新");
        }
        return flashSaleRepository.save(flashSale);
    }

    /**
     * 刪除閃購活動
     */
    @Transactional
    public void deleteFlashSale(Integer id) {
        if (!flashSaleRepository.existsById(id)) {
            throw new RuntimeException("該特賣活動不存在，無法刪除");
        }
        flashSaleRepository.deleteById(id);
    }

    /**
     * 搶購商品（Redis 前置擋流 + Conditional SQL 保底）
     */
    @Transactional
    public BigDecimal grabFlashSale(Integer flashSaleId, Integer quantity) {
        String key = STOCK_KEY + flashSaleId;

        // 第一層：Redis 原子扣減
        Long remaining = redisTemplate.opsForValue().decrement(key, quantity);
        if (remaining == null || remaining < 0) {
            redisTemplate.opsForValue().increment(key, quantity); // 補回
            throw new RuntimeException("庫存不足，商品已搶購一空！");
        }

        // 第二層：DB Conditional SQL 保底
        int affected = flashSaleRepository.decrementStock(flashSaleId, quantity);
        if (affected == 0) {
            redisTemplate.opsForValue().increment(key, quantity); // 補回
            throw new RuntimeException("庫存不足（DB 確認失敗）");
        }

        return flashSaleRepository.findById(flashSaleId)
                .map(FlashSale::getSalePrice)
                .orElseThrow(() -> new RuntimeException("活動不存在"));
    }
}