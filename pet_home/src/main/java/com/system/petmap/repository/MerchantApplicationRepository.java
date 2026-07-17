package com.system.petmap.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.system.petmap.entity.MerchantApplication;

public interface MerchantApplicationRepository
        extends JpaRepository<
                MerchantApplication,
                Integer> {

    List<MerchantApplication> findByUserId(
            Long userId);

    List<MerchantApplication> findByStatus(
            String status);
}