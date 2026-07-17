package com.system.petmap.controller;

import org.springframework.web.bind.annotation.*;

import com.system.member.dto.ApiResponse;
import com.system.member.security.annotation.RequirePermission;
import com.system.petmap.dto.PlaceTagCreateRequest;
import com.system.petmap.entity.PlaceTag;
import com.system.petmap.service.PlaceTagService;

import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/map/place-tags")
@RequiredArgsConstructor
public class PlaceTagController {

    private final PlaceTagService placeTagService;

    @RequirePermission("PLACE_TAG_RELATION_CREATE")
    @PostMapping
    public ApiResponse<PlaceTag> create(
            @RequestBody PlaceTagCreateRequest request) {

        return ApiResponse.success(
                "PLACE_TAG_CREATE_SUCCESS",
                "新增景點標籤成功",
                placeTagService.create(request));
    }

    @RequirePermission("PLACE_TAG_RELATION_DELETE")
    @DeleteMapping("/{placeId}/{tagId}")
    public ApiResponse<Void> delete(
            @PathVariable Integer placeId,
            @PathVariable Integer tagId) {

        placeTagService.delete(placeId, tagId);

        return ApiResponse.success(
                "PLACE_TAG_DELETE_SUCCESS",
                "刪除景點標籤成功",
                null);
    }
}