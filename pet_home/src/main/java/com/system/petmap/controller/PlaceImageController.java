package com.system.petmap.controller;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.system.member.dto.ApiResponse;
import com.system.member.security.annotation.RequirePermission;
import com.system.petmap.dto.PlaceImageCreateRequest;
import com.system.petmap.entity.PlaceImage;
import com.system.petmap.service.PlaceImageService;

import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/map/place-images")
@RequiredArgsConstructor
public class PlaceImageController {

        private final PlaceImageService placeImageService;

        // @RequirePermission("PLACE_IMAGE_LIST")
        @GetMapping("/place/{placeId}")
        public ApiResponse<List<PlaceImage>> findByPlaceId(
                        @PathVariable Integer placeId) {

                return ApiResponse.success(
                                "PLACE_IMAGE_LIST_SUCCESS",
                                "取得景點圖片成功",
                                placeImageService.findByPlaceId(placeId));
        }

        // @RequirePermission("PLACE_IMAGE_CREATE")
        @PostMapping
        public ApiResponse<PlaceImage> create(
                        @RequestBody PlaceImageCreateRequest request) {

                return ApiResponse.success(
                                "PLACE_IMAGE_CREATE_SUCCESS",
                                "新增景點圖片成功",
                                placeImageService.create(request));
        }

        // @RequirePermission("PLACE_IMAGE_DELETE")
        @DeleteMapping("/{imageId}")
        public ApiResponse<Void> delete(
                        @PathVariable Integer imageId) {

                placeImageService.delete(imageId);

                return ApiResponse.success(
                                "PLACE_IMAGE_DELETE_SUCCESS",
                                "刪除景點圖片成功",
                                null);
        }
}