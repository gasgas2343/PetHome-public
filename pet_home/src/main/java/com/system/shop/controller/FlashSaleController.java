package com.system.shop.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.system.member.security.annotation.RequirePermission;
import com.system.shop.dto.*;
import com.system.shop.entity.FlashSale;
import com.system.shop.service.FlashSaleService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.math.BigDecimal;
import com.system.member.security.annotation.RequirePermission;
@Tag(name = "閃購活動")
@RestController
@RequestMapping("/flash-sales")
// @CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class FlashSaleController {

    @Autowired private FlashSaleService flashSaleService;

   @GetMapping("/active")
public Result<java.util.List<FlashSaleDTO>> getActive() {
    return Result.success(flashSaleService.getActiveFlashSales());
}

    @PostMapping("/create")
    @RequirePermission("FLASHSALE_CREATE")
    public Result<FlashSale> create(@RequestBody FlashSaleCreateRequest req) {
        return Result.success(flashSaleService.createFlashSale(
                req.getProductSkuId(), req.getSalePrice(), req.getSaleStock(),
                req.getStartTime(), req.getEndTime()));
    }

    @PutMapping("/update")
    @RequirePermission("FLASHSALE_UPDATE")
    public Result<FlashSale> update(@RequestBody FlashSale flashSale) {
        return Result.success(flashSaleService.updateFlashSale(flashSale));
    }

    @DeleteMapping("/delete/{id}")
    @RequirePermission("FLASHSALE_DELETE")
    public Result<String> delete(@PathVariable Integer id) {
        flashSaleService.deleteFlashSale(id);
        return Result.success("刪除成功");
    }

    @PostMapping("/{flashSaleId}/grab")
    @RequirePermission("FLASHSALE_GRAB")
    public Result<GrabResult> grab(@PathVariable Integer flashSaleId, @RequestBody FlashSaleGrabRequest req) {
        BigDecimal price = flashSaleService.grabFlashSale(flashSaleId, req.getQuantity());
        return Result.success(new GrabResult(flashSaleId, req.getQuantity(), price));
    }
    @GetMapping("/all")
    @RequirePermission("FLASHSALE_READ_ALL")
public Result<java.util.List<FlashSale>> getAll() {
    return Result.success(flashSaleService.getAllFlashSales());
}
}