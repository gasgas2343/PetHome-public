package com.system.shop.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.system.shop.dto.Result;
import com.system.shop.entity.Brand;
import com.system.shop.service.BrandService;

import java.util.List;

@Tag(name = "品牌管理", description = "品牌查詢 API")
@RestController
// @CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@RequestMapping("/brands")
public class BrandController {

    @Autowired
    private BrandService brandService;

    @Operation(summary = "查詢所有品牌")
    @GetMapping
    public Result<List<Brand>> listAll() {
        return Result.success(brandService.findAll());
    }

    @Operation(summary = "查詢單一品牌")
    @GetMapping("/{id}")
    public Result<Brand> getById(@PathVariable Integer id) {
        return Result.success(brandService.findById(id));
    }
}
