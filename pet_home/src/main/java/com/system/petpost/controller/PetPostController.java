package com.system.petpost.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.system.member.security.annotation.RequirePermission;
import com.system.petpost.dto.PetPostInsertDTO;
import com.system.petpost.dto.PetPostResponseDTO;
import com.system.petpost.dto.PetPostUpdateDTO;
import com.system.petpost.service.PetPostService;
import com.system.petpost.util.LoginUserUtil;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/pet-posts")
@RequiredArgsConstructor
public class PetPostController {

        private final PetPostService petPostService;

        @GetMapping
        public ResponseEntity<List<PetPostResponseDTO>> findAll() {
                return ResponseEntity.ok(
                                petPostService.findAll());
        }

        @GetMapping("/pet/{petId}")
        public ResponseEntity<List<PetPostResponseDTO>> findByPetId(
                        @PathVariable Long petId) {

                return ResponseEntity.ok(
                                petPostService.findByPetId(petId));
        }

        @GetMapping("/{petPostId}")
        public ResponseEntity<PetPostResponseDTO> findById(
                        @PathVariable Long petPostId) {

                return ResponseEntity.ok(
                                petPostService.findById(petPostId));
        }

        /**
         * 中文註解：新增時間軸。
         * 使用 PET_POST_CREATE 權限，並從 JWT 取得目前登入者。
         */
        @PostMapping
        @RequirePermission("PET_POST_CREATE")
        public ResponseEntity<PetPostResponseDTO> create(
                        @Valid @RequestBody PetPostInsertDTO dto) {

                Long userId = LoginUserUtil.getLoginUserId();

                return ResponseEntity.ok(
                                petPostService.create(dto, userId));
        }

        /**
         * 中文註解：修改時間軸。
         * 使用 PET_POST_UPDATE 權限，Service 需檢查這筆時間軸是否屬於目前登入者。
         */
        @PutMapping("/{petPostId}")
        @RequirePermission("PET_POST_UPDATE")
        public ResponseEntity<PetPostResponseDTO> update(
                        @PathVariable Long petPostId,
                        @Valid @RequestBody PetPostUpdateDTO dto) {

                Long userId = LoginUserUtil.getLoginUserId();

                return ResponseEntity.ok(
                                petPostService.update(petPostId, dto, userId));
        }

        /**
         * 中文註解：上傳時間軸本地圖片。
         */
        @PostMapping("/images/upload")
        @RequirePermission("PET_POST_CREATE")
        public ResponseEntity<Map<String, String>> uploadImage(
                        @RequestParam("file") MultipartFile file) {

                String imageUrl = petPostService.uploadImage(file);

                return ResponseEntity.ok(
                                Map.of("imageUrl", imageUrl));
        }

        /**
         * 中文註解：刪除時間軸。
         * 使用 PET_POST_DELETE 權限，Service 需檢查這筆時間軸是否屬於目前登入者。
         */
        @DeleteMapping("/{petPostId}")
        @RequirePermission("PET_POST_DELETE")
        public ResponseEntity<String> delete(
                        @PathVariable Long petPostId) {

                Long userId = LoginUserUtil.getLoginUserId();

                petPostService.delete(petPostId, userId);

                return ResponseEntity.ok("時間軸刪除成功");
        }
}