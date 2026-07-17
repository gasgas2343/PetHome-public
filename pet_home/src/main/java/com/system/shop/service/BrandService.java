package com.system.shop.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.system.shop.entity.Brand;
import com.system.shop.repository.BrandRepository;

import java.util.List;

@Service
public class BrandService {

    @Autowired
    private BrandRepository brandRepository;

    public List<Brand> findAll() {
        return brandRepository.findAll();
    }

    public Brand findById(Integer id) {
        return brandRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("品牌不存在：" + id));
    }
}
