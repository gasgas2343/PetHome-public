package com.system.petmap.controller;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.system.member.dto.ApiResponse;
import com.system.member.security.annotation.RequirePermission;
import com.system.petmap.dto.TagCreateRequest;
import com.system.petmap.dto.TagUpdateRequest;
import com.system.petmap.entity.Place;
import com.system.petmap.entity.Tag;
import com.system.petmap.service.PlaceTagService;
import com.system.petmap.service.TagService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/map/tags")
@RequiredArgsConstructor
public class TagController {

        private final TagService tagService;

        private final PlaceTagService placeTagService;

        @RequirePermission("TAG_LIST")
        @GetMapping
        public ApiResponse<List<Tag>> findAll() {

                return ApiResponse.success(
                                "TAG_LIST_SUCCESS",
                                "取得 Tag 成功",
                                tagService.findAll());

        }

        @RequirePermission("TAG_GET")
        @GetMapping("/{id}")
        public ApiResponse<Tag> findById(
                        @PathVariable Integer id) {

                return ApiResponse.success(
                                "TAG_GET_SUCCESS",
                                "取得 Tag 成功",
                                tagService.findById(id));

        }

        @RequirePermission("TAG_CREATE")
        @PostMapping
        public ApiResponse<Tag> create(
                        @RequestBody TagCreateRequest request) {

                return ApiResponse.success(
                                "TAG_CREATE_SUCCESS",
                                "新增 Tag 成功",
                                tagService.create(request));

        }

        @RequirePermission("TAG_UPDATE")
        @PutMapping("/{id}")
        public ApiResponse<Tag> update(
                        @PathVariable Integer id,
                        @RequestBody TagUpdateRequest request) {

                return ApiResponse.success(
                                "TAG_UPDATE_SUCCESS",
                                "修改 Tag 成功",
                                tagService.update(id, request));

        }

        @RequirePermission("TAG_DELETE")
        @DeleteMapping("/{id}")
        public ApiResponse<Void> delete(
                        @PathVariable Integer id) {

                tagService.delete(id);

                return ApiResponse.success(
                                "TAG_DELETE_SUCCESS",
                                "刪除 Tag 成功",
                                null);

        }

        @RequirePermission("TAG_PLACE_LIST")
        @GetMapping("/{tagId}/places")
        public ApiResponse<List<Place>> findPlacesByTagId(
                        @PathVariable Integer tagId) {

                return ApiResponse.success(
                                "TAG_PLACE_LIST_SUCCESS",
                                "取得 Tag 景點成功",
                                placeTagService.findPlacesByTagId(tagId));

        }
}
