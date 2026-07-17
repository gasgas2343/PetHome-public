package com.system.shop.controller;

import io.swagger.v3.oas.annotations.Operation;

import com.system.member.security.annotation.RequirePermission;
import com.system.shop.dto.ProductSaveRequest;
import com.system.shop.dto.Result;
import com.system.shop.entity.Category;
import com.system.shop.entity.Product;
import com.system.shop.entity.ProductSku;
import com.system.shop.service.ProductService;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import com.system.member.security.annotation.RequirePermission;

@Tag(name = "商品管理", description = "商品查詢、SKU、分類、品牌相關 API")
@RestController
// @CrossOrigin(origins = "http://localhost:5173")
// @CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@RequestMapping("/products")
public class ProductController {

    @Autowired private ProductService productService;

    // --- 原有功能保持不變 ---
    @Operation(summary = "查詢所有商品（包含規格與描述）")
@GetMapping
public Result<List<Product>> listAll() {
    List<Product> products = productService.findAll();
    // 遍歷商品，把各自的 SKUs 查出來放進去（如果 Product 實體內有 @Transient private List<ProductSku> skus 欄位的話）
    // 如果沒有，沒關係！我們直接在前端發送請求，或者保持原本的 findAll()。
    return Result.success(products);
}

    @Operation(summary = "查詢單一商品")
    @GetMapping("/{id}")
    public Result<Product> getById(@PathVariable Integer id) {
        Product p = productService.findById(id);
        System.out.println("DEBUG: 從資料庫抓到的商品名稱: " + p.getName());
        System.out.println("DEBUG: 從資料庫抓到的圖片網址: " + p.getImageUrl());
        return Result.success(productService.findById(id));
    }

    @Operation(summary = "查詢商品 SKU 列表")
    @GetMapping("/{id}/skus")
    public Result<List<ProductSku>> getSkus(@PathVariable Integer id) {
        return Result.success(productService.findSkusByProductId(id));
    }

    // --- 以下為修正後的功能 ---

    @Operation(summary = "查詢所有分類(樹狀結構)")
    @GetMapping("/categories")
    public Result<List<Category>> listCategories() {
        // 此方法回傳包含 children 子列表的樹狀資料
        return Result.success(productService.findAllCategoriesTree());
    }

    @Operation(summary = "依分類 ID 查詢商品")
    @GetMapping("/category/{categoryId}")
    public Result<List<Product>> getByCategory(
            @Parameter(description = "分類 ID", example = "1") @PathVariable Integer categoryId) {
        return Result.success(productService.findByCategoryId(categoryId));
    }

    @Operation(summary = "依品牌查詢商品")
    @GetMapping("/brand/{brandId}")
    public Result<List<Product>> getByBrand(
            @Parameter(description = "品牌 ID", example = "1") @PathVariable Integer brandId) {
        return Result.success(productService.findByBrandId(brandId));
    }
    // 在 ProductController.java 中新增以下方法

@Operation(summary = "新增商品（包含自訂多規格與描述）")
@RequirePermission("PRODUCT_CREATE")
@PostMapping
public Result<Product> addProduct(@RequestBody com.system.shop.dto.ProductSaveRequest productSaveRequest) {
    // 呼叫包含規格處理邏輯的新 Service 方法
    return Result.success(productService.saveProductWithSkus(productSaveRequest));
}

@Operation(summary = "更新商品")
@RequirePermission("PRODUCT_UPDATE")

@PutMapping("/{id}")
public Result<Product> updateProduct(@PathVariable Integer id, @RequestBody Product product) {
    product.setId(id); // 確保更新的是正確的 ID
    return Result.success(productService.update(product));
}

@Operation(summary = "刪除商品")
@RequirePermission("PRODUCT_DELETE")
@DeleteMapping("/{id}")
public Result<String> deleteProduct(@PathVariable Integer id) {
    productService.deleteById(id);
    return Result.success("刪除成功");
}
@CrossOrigin(origins = "http://localhost:5173")
@Operation(summary = "商品圖片上傳")
@RequirePermission("PRODUCT_UPLOAD")
@PostMapping("/upload")
public Result<String> uploadImage(@RequestParam("file") org.springframework.web.multipart.MultipartFile file) {
    try {
        String fileUrl = productService.uploadToImgBB(file); 
        return Result.success(fileUrl);
    } catch (Exception e) {
    // 傳入 500 代表伺服器錯誤，後接錯誤訊息
    return Result.error(500, "圖片上傳失敗: " + e.getMessage());
}

}
@PutMapping("/{id}/status")
@RequirePermission("PRODUCT_STATUS_UPDATE")
public Result<Void> updateStatus(@PathVariable Integer id, @RequestParam Integer status) {
    productService.updateStatus(id, status);
    return Result.success(null);
}

// 💡 新增：處理單一商品規格細項 (SKU) 修改的 API 接口
@Operation(summary = "修改單一規格細項 (SKU)")
@RequirePermission("PRODUCT_SKU_UPDATE")
@PutMapping("/skus/{skuId}")
public Result<String> updateProductSku(@PathVariable Integer skuId, @RequestBody ProductSku productSku) {
    try {
        // 確保設定的是正確的 SKU ID
        productSku.setId(skuId);
        
        // 💡 呼叫 Service 層執行更新作業 
        // （請確認你的 productService 內是否有內建 updateSku 方法，
        //   若名稱不同，請修改為對應的方法名稱，例如 productService.saveSku(productSku)）
        productService.updateSku(productSku);
        
        return Result.success("規格修改成功");
    } catch (Exception e) {
        e.printStackTrace(); // 在終端機印出詳細 Java 錯誤日誌
        return Result.error(500, "後端更新規格失敗: " + e.getMessage());
    }
}
}