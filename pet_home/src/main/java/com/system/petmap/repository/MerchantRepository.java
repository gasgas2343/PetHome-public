package com.system.petmap.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.system.petmap.entity.Merchant;

public interface MerchantRepository
        extends JpaRepository<
                Merchant,
                Long> {

    List<Merchant> findByOwnerUserId(
            Long userId);
}
