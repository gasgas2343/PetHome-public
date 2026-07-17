package com.system.petpost.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.system.petpost.entity.PetPost;

/**
 * 寵物時間軸 Repository
 *
 * 負責操作 pet_pet_posts 資料表
 */
public interface PetPostRepository
        extends JpaRepository<PetPost, Long> {

    /**
     * 查詢全部時間軸，依照日期由舊到新排序
     *
     * 對應時間軸顯示：
     * ●────●────●────○
     */
    List<PetPost> findAllByOrderByPostDateAsc();

    /**
     * 查詢某一隻毛孩的時間軸，依照日期由舊到新排序
     *
     * 如果之後每隻寵物都有自己的時間軸，會用這個
     */
    List<PetPost> findByPetIdOrderByPostDateAsc(
            Long petId);
}