package com.system.petpost.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.system.petpost.entity.Pet;

public interface ForumPetRepository
        extends JpaRepository<Pet, Long> {

    // 查詢會員所有毛孩
    List<Pet> findByUserId(
            Long userId);

    // 搜尋毛孩名稱
    List<Pet> findByPetNameContaining(
            String keyword);

}