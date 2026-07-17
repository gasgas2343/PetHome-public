// package com.system.member.shop.service;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Service;
// import com.system.member.shop.entity.Product;
// import com.system.member.shop.entity.ProductSku;
// import com.system.member.shop.entity.Category; // 確保已匯入
// import com.system.member.shop.repository.ProductRepository;
// import com.system.member.shop.repository.ProductSkuRepository;
// import com.system.member.shop.repository.CategoryRepository; // 確保您有此 Repository

// import java.util.List;
// import java.util.stream.Collectors;

// @Service
// public class ProductService {

//     @Autowired private ProductRepository productRepository;
//     @Autowired private ProductSkuRepository productSkuRepository;
//     @Autowired private CategoryRepository categoryRepository; // 新增分類的 Repository

//     // --- 既有功能 ---
//     public List<Product> findAll() { return productRepository.findAll(); }
//     public Product findById(Integer id) {
//         return productRepository.findById(id)
//                 .orElseThrow(() -> new RuntimeException("商品不存在: " + id));
//     }
//     public List<ProductSku> findSkusByProductId(Integer productId) {
//         return productSkuRepository.findByProductId(productId);
//     }
//     public List<Product> findByBrandId(Integer brandId) {
//         return productRepository.findByBrandId(brandId);
//     }

//     // --- 修正後的分類查詢功能 ---

//     // 1. 取得樹狀結構分類
//     public List<Category> findAllCategoriesTree() {
//         List<Category> all = categoryRepository.findAll();
//         // 將平面列表轉換為樹狀結構
//         return all.stream()
//                 .filter(c -> c.getParentId() == null) // 找出根節點
//                 .peek(c -> c.setChildren(all.stream()
//                         .filter(sub -> sub.getParentId() != null && sub.getParentId().equals(c.getId()))
//                         .collect(Collectors.toList())))
//                 .collect(Collectors.toList());
//     }

//     // 2. 依分類 ID 查詢商品
//     public List<Product> findByCategoryId(Integer categoryId) {
//         // 假設 ProductRepository 中有 findByCategoryId 方法
//         return productRepository.findByCategoryId(categoryId);
//     }

//     // --- 新增 CRUD 管理功能 ---

// // 1. 新增或更新商品 (save 既可以用於新增，也可以更新已存在的商品)
// public Product save(Product product) {
//     return productRepository.save(product);
// }

// // 2. 更新商品 (明確定義一個 update 方法，邏輯與 save 類似)
// public Product update(Product product) {
//     // 檢查是否存在，不存在則拋出例外
//     if (!productRepository.existsById(product.getId())) {
//         throw new RuntimeException("無法更新，商品不存在: " + product.getId());
//     }
//     return productRepository.save(product);
// }

// // 3. 刪除商品
// public void deleteById(Integer id) {
//     if (!productRepository.existsById(id)) {
//         throw new RuntimeException("無法刪除，商品不存在: " + id);
//     }
//     productRepository.deleteById(id);
// }
// }
package com.system.shop.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.client.RestTemplate;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.http.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.system.shop.dto.ProductSaveRequest;
import com.system.shop.entity.Category;
import com.system.shop.entity.Product;
import com.system.shop.entity.ProductSku;
import com.system.shop.repository.CategoryRepository;
import com.system.shop.repository.ProductRepository;
import com.system.shop.repository.ProductSkuRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.transaction.annotation.Transactional;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {

    @Autowired private ProductRepository productRepository;
    @Autowired private ProductSkuRepository productSkuRepository;
    @Autowired private CategoryRepository categoryRepository;
    @Autowired private RestTemplate restTemplate;

    // --- 既有功能 ---
    public List<Product> findAll() { return productRepository.findAll(); }
    public Product findById(Integer id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("商品不存在: " + id));
    }
    public List<ProductSku> findSkusByProductId(Integer productId) {
        return productSkuRepository.findByProductId(productId);
    }
    public List<Product> findByBrandId(Integer brandId) {
        return productRepository.findByBrandId(brandId);
    }

    // --- 分類查詢 ---
    public List<Category> findAllCategoriesTree() {
        List<Category> all = categoryRepository.findAll();
        return all.stream()
                .filter(c -> c.getParentId() == null)
                .peek(c -> c.setChildren(all.stream()
                        .filter(sub -> sub.getParentId() != null && sub.getParentId().equals(c.getId()))
                        .collect(Collectors.toList())))
                .collect(Collectors.toList());
    }

    public List<Product> findByCategoryId(Integer categoryId) {
        return productRepository.findByCategoryId(categoryId);
    }

    // --- CRUD 管理功能 ---
   @Transactional
public Product save(Product product) {
    Product saved = productRepository.save(product);
    
    // 自動幫新商品建立一筆預設 SKU
    boolean hasSkus = !productSkuRepository.findByProductId(saved.getId()).isEmpty();
    if (!hasSkus) {
        ProductSku sku = new ProductSku();
        sku.setProductId(saved.getId());
        sku.setSkuCode("SKU-" + saved.getId());
        sku.setPrice(saved.getBasePrice());
        sku.setStock(0);
        sku.setReservedStock(0);
        productSkuRepository.save(sku);
    }
    
    return saved;
}

    public Product update(Product product) {
        if (product.getId() == null || !productRepository.existsById(product.getId())) {
            throw new RuntimeException("無法更新，商品不存在: " + (product.getId() != null ? product.getId() : "null"));
        }
        return productRepository.save(product);
    }

    public void deleteById(Integer id) {
        if (!productRepository.existsById(id)) {
            throw new RuntimeException("無法刪除，商品不存在: " + id);
        }
        productRepository.deleteById(id);
    }

    // --- ImgBB 圖片上傳功能 ---
public String uploadToImgBB(MultipartFile file) {
    try {
        String apiKey = "9df264fc5506eaefff421e887cb36423";
        String url = "https://api.imgbb.com/1/upload?key=" + apiKey;

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        // 確保 Base64 字串正確
        body.add("image", Base64.getEncoder().encodeToString(file.getBytes()));

        HttpHeaders headers = new HttpHeaders();
        // 修正：ImgBB 其實比較喜歡這種格式
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);
        
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(response.getBody());
        
        // 如果 API 呼叫成功，這裡會回傳圖片網址
        if (rootNode.has("data") && rootNode.get("data").has("url")) {
            return rootNode.path("data").path("url").asText();
        } else {
            throw new RuntimeException("ImgBB API 回應格式異常: " + response.getBody());
        }
        
    } catch (Exception e) {
        // 請務必複製這個產生的紅字給我
        e.printStackTrace(); 
        throw new RuntimeException("圖片上傳失敗: " + e.getMessage());
    }
}
@Transactional
public void updateStatus(Integer id, Integer status) {
    // 1. 先從資料庫取出該商品
    Product product = productRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("商品不存在: " + id));
    
    // 2. 更新狀態
    product.setStatus(status);
    
    // 3. 儲存回資料庫
    productRepository.save(product);
}

@Transactional
public Product saveProductWithSkus(com.system.shop.dto.ProductSaveRequest req) {
    // 1. 先把主商品資料複製到 Product Entity 並儲存
    Product product = new Product();
    product.setName(req.getName());
    product.setDescription(req.getDescription()); // 寫入大格子描述
    product.setBasePrice(req.getBasePrice());
    product.setBrandId(req.getBrandId());
    product.setCategoryId(req.getCategoryId());
    product.setImageUrl(req.getImageUrl());
    product.setStatus(req.getStatus() != null ? req.getStatus() : 1);
    
    Product savedProduct = productRepository.save(product);

    // 2. 處理前端傳進來的自訂規格陣列 (SKU)
    if (req.getSkus() != null && !req.getSkus().isEmpty()) {
        for (ProductSku skuReq : req.getSkus()) {
            ProductSku sku = new ProductSku();
            sku.setProductId(savedProduct.getId()); // 綁定剛剛建立成功的商品 ID
            sku.setSkuCode(skuReq.getSkuCode());
            sku.setPrice(skuReq.getPrice());
            sku.setStock(skuReq.getStock());
            sku.setReservedStock(skuReq.getReservedStock() != null ? skuReq.getReservedStock() : 0);
            
            // 🔥 精準存入！解決資料庫變成 NULL 的病灶
            sku.setSize(skuReq.getSize());
            sku.setFlavor(skuReq.getFlavor());

            productSkuRepository.save(sku);
        }
    } else {
        // 如果前端完全沒填規格，才給一筆系統預設的
        ProductSku defaultSku = new ProductSku();
        defaultSku.setProductId(savedProduct.getId());
        defaultSku.setSkuCode("SKU-" + savedProduct.getId());
        defaultSku.setPrice(savedProduct.getBasePrice());
        defaultSku.setStock(0);
        defaultSku.setReservedStock(0);
        productSkuRepository.save(defaultSku);
    }

    return savedProduct;
}
// 💡 新增：修改單一規格細項 (SKU) 的 Service 邏輯
@Transactional
public void updateSku(ProductSku productSku) {
    if (productSku.getId() == null || !productSkuRepository.existsById(productSku.getId())) {
        throw new RuntimeException("無法更新規格，該 SKU 不存在: " + (productSku.getId() != null ? productSku.getId() : "null"));
    }
    // 透過 JPA Repository 直接儲存更新後的規格資料 (包含前端傳入的最新 price, stock, flavor, size 等)
    productSkuRepository.save(productSku);
}
}