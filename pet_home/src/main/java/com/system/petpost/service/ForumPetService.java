package com.system.petpost.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.system.member.exception.BusinessException;
import com.system.petpost.dto.PetCreateDTO;
import com.system.petpost.entity.Pet;
import com.system.petpost.repository.ForumPetRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ForumPetService {

    private final ForumPetRepository petRepository;

    /**
     * 中文註解：新增毛孩。
     * userId 由 Controller 從 JWT 取得，不接受前端傳入。
     */
    public Pet create(PetCreateDTO dto, Long userId) {

        if (dto == null) {
            throw new BusinessException("PET_BODY_REQUIRED", "毛孩資料不可為空");
        }

        if (userId == null) {
            throw new BusinessException("USER_ID_REQUIRED", "會員ID不可為空");
        }

        LocalDateTime now = LocalDateTime.now();

        Pet pet = new Pet();

        pet.setUserId(userId);
        pet.setPetName(dto.getPetName());
        pet.setPetIntro(dto.getPetIntro());
        pet.setPetAvatarUrl(dto.getPetAvatarUrl());
        pet.setCreatedAt(now);
        pet.setUpdatedAt(now);

        return petRepository.save(pet);
    }

    /**
     * 中文註解：查詢全部毛孩。
     * 目前保留給測試或公開展示用。
     */
    public List<Pet> findAll() {
        return petRepository.findAll();
    }

    /**
     * 中文註解：查詢某會員所有毛孩。
     */
    public List<Pet> findByUserId(Long userId) {

        if (userId == null) {
            throw new BusinessException("USER_ID_REQUIRED", "會員ID不可為空");
        }

        return petRepository.findByUserId(userId);
    }

    /**
     * 中文註解：查詢單筆毛孩。
     */
    public Pet findById(Long petId) {

        if (petId == null) {
            throw new BusinessException("PET_ID_REQUIRED", "毛孩ID不可為空");
        }

        return petRepository.findById(petId)
                .orElseThrow(() ->
                        new BusinessException("PET_NOT_FOUND", "毛孩不存在"));
    }

    /**
     * 中文註解：修改毛孩。
     * 只能修改自己的毛孩資料。
     */
    public Pet update(Long petId, PetCreateDTO dto, Long userId) {

        if (dto == null) {
            throw new BusinessException("PET_BODY_REQUIRED", "毛孩資料不可為空");
        }

        Pet pet = findById(petId);

        if (!pet.getUserId().equals(userId)) {
            throw new BusinessException(
                    "PET_OWNER_REQUIRED",
                    "只能修改自己的寵物資料");
        }

        pet.setPetName(dto.getPetName());
        pet.setPetIntro(dto.getPetIntro());
        pet.setPetAvatarUrl(dto.getPetAvatarUrl());
        pet.setUpdatedAt(LocalDateTime.now());

        return petRepository.save(pet);
    }

    /**
     * 中文註解：刪除毛孩。
     * 只能刪除自己的毛孩資料。
     */
    public void delete(Long petId, Long userId) {

        Pet pet = findById(petId);

        if (!pet.getUserId().equals(userId)) {
            throw new BusinessException(
                    "PET_OWNER_REQUIRED",
                    "只能刪除自己的寵物資料");
        }

        petRepository.delete(pet);
    }
}