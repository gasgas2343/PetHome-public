package com.system.petpost.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.system.member.exception.BusinessException;
import com.system.petpost.dto.PetPostInsertDTO;
import com.system.petpost.dto.PetPostResponseDTO;
import com.system.petpost.dto.PetPostUpdateDTO;
import com.system.petpost.entity.Pet;
import com.system.petpost.entity.PetPost;
import com.system.petpost.exception.ResourceNotFoundException;
import com.system.petpost.repository.ForumPetRepository;
import com.system.petpost.repository.PetPostRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PetPostService {

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    private final PetPostRepository petPostRepository;

    private final ForumPetRepository petRepository;

    /**
     * 中文註解：查詢全部時間軸。
     */
    public List<PetPostResponseDTO> findAll() {

        return petPostRepository
                .findAllByOrderByPostDateAsc()
                .stream()
                .map(this::convertToDTO)
                .toList();
    }

    /**
     * 中文註解：查詢某一隻毛孩的時間軸。
     */
    public List<PetPostResponseDTO> findByPetId(Long petId) {

        if (petId == null) {
            throw new BusinessException("PET_ID_REQUIRED", "毛孩ID不可為空");
        }

        return petPostRepository
                .findByPetIdOrderByPostDateAsc(petId)
                .stream()
                .map(this::convertToDTO)
                .toList();
    }

    /**
     * 中文註解：查詢單一時間軸。
     */
    public PetPostResponseDTO findById(Long petPostId) {

        PetPost petPost = findEntityById(petPostId);

        return convertToDTO(petPost);
    }

    /**
     * 中文註解：新增時間軸。
     * 只能新增自己毛孩的時間軸。
     */
    public PetPostResponseDTO create(PetPostInsertDTO dto, Long userId) {

        if (dto == null) {
            throw new BusinessException("PET_POST_BODY_REQUIRED", "時間軸資料不可為空");
        }

        if (userId == null) {
            throw new BusinessException("USER_ID_REQUIRED", "會員ID不可為空");
        }

        if (dto.getPetId() == null) {
            throw new BusinessException(
                    "PET_NOT_FOUND",
                    "尚未建立毛孩資料，請先到「毛孩資料」新增毛孩後，再新增回憶紀錄");
        }

        Pet pet = petRepository.findById(dto.getPetId())
                .orElseThrow(() ->
                        new BusinessException(
                                "PET_NOT_FOUND",
                                "尚未建立毛孩資料，請先到「毛孩資料」新增毛孩後，再新增回憶紀錄"));

        if (!pet.getUserId().equals(userId)) {
            throw new BusinessException(
                    "PET_OWNER_REQUIRED",
                    "只能新增自己毛孩的時間軸");
        }

        LocalDateTime now = LocalDateTime.now();

        PetPost petPost = new PetPost();

        petPost.setPetId(dto.getPetId());
        petPost.setTitle(dto.getTitle());
        petPost.setContent(dto.getContent());
        petPost.setPostDate(dto.getPostDate());
        petPost.setImageUrl(dto.getImageUrl());
        petPost.setCreatedAt(now);
        petPost.setUpdatedAt(now);

        PetPost savedPetPost = petPostRepository.save(petPost);

        return convertToDTO(savedPetPost);
    }

    /**
     * 中文註解：修改時間軸。
     * 只能修改自己毛孩的時間軸。
     */
    public PetPostResponseDTO update(
            Long petPostId,
            PetPostUpdateDTO dto,
            Long userId) {

        if (dto == null) {
            throw new BusinessException("PET_POST_BODY_REQUIRED", "時間軸資料不可為空");
        }

        if (userId == null) {
            throw new BusinessException("USER_ID_REQUIRED", "會員ID不可為空");
        }

        PetPost petPost = findEntityById(petPostId);

        validatePetPostOwner(petPost, userId);

        petPost.setTitle(dto.getTitle());
        petPost.setContent(dto.getContent());
        petPost.setPostDate(dto.getPostDate());
        petPost.setImageUrl(dto.getImageUrl());
        petPost.setUpdatedAt(LocalDateTime.now());

        PetPost savedPetPost = petPostRepository.save(petPost);

        return convertToDTO(savedPetPost);
    }

    /**
     * 中文註解：刪除時間軸。
     * 只能刪除自己毛孩的時間軸。
     */
    public void delete(Long petPostId, Long userId) {

        if (userId == null) {
            throw new BusinessException("USER_ID_REQUIRED", "會員ID不可為空");
        }

        PetPost petPost = findEntityById(petPostId);

        validatePetPostOwner(petPost, userId);

        petPostRepository.delete(petPost);
    }

    /**
     * 中文註解：查詢 Entity。
     */
    private PetPost findEntityById(Long petPostId) {

        if (petPostId == null) {
            throw new BusinessException("PET_POST_ID_REQUIRED", "時間軸ID不可為空");
        }

        return petPostRepository
                .findById(petPostId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("時間軸不存在"));
    }

    /**
     * 中文註解：檢查時間軸是否屬於目前登入者的毛孩。
     */
    private void validatePetPostOwner(PetPost petPost, Long userId) {

        Pet pet = petRepository.findById(petPost.getPetId())
                .orElseThrow(() ->
                        new BusinessException("PET_NOT_FOUND", "毛孩不存在"));

        if (!pet.getUserId().equals(userId)) {
            throw new BusinessException(
                    "PET_POST_OWNER_REQUIRED",
                    "只能操作自己毛孩的時間軸");
        }
    }

    /**
     * 中文註解：上傳本地圖片。
     */
    public String uploadImage(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new BusinessException("IMAGE_REQUIRED", "圖片不可為空");
        }

        String contentType = file.getContentType();

        if (contentType == null || !contentType.startsWith("image/")) {
            throw new BusinessException("IMAGE_TYPE_INVALID", "只能上傳圖片檔案");
        }

        try {
            Path uploadPath = Paths.get(uploadDir).toAbsolutePath();

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String originalFilename = file.getOriginalFilename();
            String extension = "";

            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(
                        originalFilename.lastIndexOf("."));
            }

            String filename = UUID.randomUUID() + extension;

            Path targetPath = uploadPath.resolve(filename);

            file.transferTo(targetPath.toFile());

            return "/uploads/" + filename;

        } catch (IOException e) {
            throw new BusinessException("IMAGE_UPLOAD_FAILED", "圖片上傳失敗");
        }
    }

    /**
     * 中文註解：Entity 轉 ResponseDTO。
     */
    private PetPostResponseDTO convertToDTO(PetPost petPost) {

        PetPostResponseDTO dto = new PetPostResponseDTO();

        dto.setPetPostId(petPost.getPetPostId());
        dto.setPetId(petPost.getPetId());
        dto.setTitle(petPost.getTitle());
        dto.setContent(petPost.getContent());
        dto.setPostDate(petPost.getPostDate());
        dto.setImageUrl(petPost.getImageUrl());

        return dto;
    }
}