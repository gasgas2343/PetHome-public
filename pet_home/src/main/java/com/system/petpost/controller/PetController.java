
package com.system.petpost.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.system.member.security.annotation.RequirePermission;
import com.system.petpost.dto.PetCreateDTO;
import com.system.petpost.entity.Pet;
import com.system.petpost.service.ForumPetService;
import com.system.petpost.util.LoginUserUtil;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

// 寵物控制器
//
// 功能：
// 1. 新增寵物
// 2. 查詢會員所有寵物
// 3. 查詢單隻寵物
// 4. 修改寵物資料
// 5. 刪除寵物
@RestController
@RequestMapping("/pets")
@RequiredArgsConstructor
public class PetController {

        // 寵物服務
        private final ForumPetService petService;

        // 新增寵物
        @PostMapping
        @RequirePermission("PET_CREATE")
        public ResponseEntity<Pet> create(
                        @Valid @RequestBody PetCreateDTO dto) {

                Long userId = LoginUserUtil.getLoginUserId();

                return ResponseEntity.ok(
                                petService.create(dto, userId));
        }

        // 查詢全部寵物：前端測試用，正式登入後建議使用 /api/pets/user/{userId}
        @GetMapping
        public ResponseEntity<List<Pet>> findAll() {
                return ResponseEntity.ok(petService.findAll());
        }

        // 查詢會員所有寵物
        @GetMapping("/me")
        public ResponseEntity<List<Pet>> findMyPets() {

                Long userId = LoginUserUtil.getLoginUserId();

                return ResponseEntity.ok(
                                petService.findByUserId(userId));
        }

        // 查詢單隻寵物
        @GetMapping("/{petId}")
        public ResponseEntity<Pet> findById(

                        @PathVariable Long petId) {

                return ResponseEntity.ok(
                                petService.findById(
                                                petId));
        }

        // 修改寵物資料
        @PutMapping("/{petId}")
        @RequirePermission("PET_UPDATE")
        public ResponseEntity<Pet> update(
                        @PathVariable Long petId,
                        @Valid @RequestBody PetCreateDTO dto) {
                Long userId = LoginUserUtil.getLoginUserId();
                return ResponseEntity.ok(
                                petService.update(
                                                petId,
                                                dto,
                                                userId));
        }

        // 刪除寵物
        @DeleteMapping("/{petId}")
        @RequirePermission("PET_DELETE")
        public ResponseEntity<String> delete(
                        @PathVariable Long petId) {
                Long userId = LoginUserUtil.getLoginUserId();
                petService.delete(
                                petId,
                                userId);
                return ResponseEntity.ok("寵物刪除成功");
        }
}