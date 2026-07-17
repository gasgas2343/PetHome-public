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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.system.member.dto.ApiResponse;
import com.system.member.security.annotation.RequirePermission;
import com.system.petmap.dto.PlaceCreateRequest;
import com.system.petmap.dto.PlaceSearchResponse;
import com.system.petmap.dto.PlaceUpdateRequest;
import com.system.petmap.entity.Place;
import com.system.petmap.entity.Tag;
import com.system.petmap.service.PlaceService;
import com.system.petmap.service.PlaceTagService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/map/places")
@RequiredArgsConstructor
public class PlaceController {

        private final PlaceTagService placeTagService;

        private final PlaceService placeService;

        @RequirePermission("PLACE_LIST")
        @GetMapping
        public ApiResponse<List<Place>> findAll() {

                return ApiResponse.success(
                                "PLACE_LIST_SUCCESS",
                                "取得景點成功",
                                placeService.findAll());
        }

        @RequirePermission("PLACE_GET")
        @GetMapping("/{id}")
        public ApiResponse<Place> findById(
                        @PathVariable Integer id) {

                return ApiResponse.success(
                                "PLACE_GET_SUCCESS",
                                "取得景點成功",
                                placeService.findById(id));
        }

        @RequirePermission("PLACE_CREATE")
        @PostMapping
        public ApiResponse<Place> create(
                        @RequestBody PlaceCreateRequest request) {

                return ApiResponse.success(
                                "PLACE_CREATE_SUCCESS",
                                "新增景點成功",
                                placeService.create(request));
        }

        @RequirePermission("PLACE_UPDATE")
        @PutMapping("/{id}")
        public ApiResponse<Place> update(
                        @PathVariable Integer id,
                        @RequestBody PlaceUpdateRequest request) {

                return ApiResponse.success(
                                "PLACE_UPDATE_SUCCESS",
                                "修改景點成功",
                                placeService.update(id, request));
        }

        @RequirePermission("PLACE_DELETE")
        @DeleteMapping("/{id}")
        public ApiResponse<Void> delete(
                        @PathVariable Integer id) {

                placeService.delete(id);

                return ApiResponse.success(
                                "PLACE_DELETE_SUCCESS",
                                "刪除景點成功",
                                null);
        }

        @RequirePermission("PLACE_TAG_LIST")
        @GetMapping("/{placeId}/tags")
        public ApiResponse<List<Tag>> findTagsByPlaceId(
                        @PathVariable Integer placeId) {

                return ApiResponse.success(
                                "PLACE_TAG_LIST_SUCCESS",
                                "取得景點標籤成功",
                                placeTagService.findTagsByPlaceId(placeId));
        }

        @RequirePermission("PLACE_SEARCH")
        @GetMapping("/search")
        public ApiResponse<List<PlaceSearchResponse>> search(

                        @RequestParam(required = false) String placeType,

                        @RequestParam(required = false) List<Integer> tagIds,

                        @RequestParam(required = false) String keyword) {

                List<Place> places = placeService.search(
                                placeType,
                                tagIds,
                                keyword);

                return ApiResponse.success(
                                "PLACE_SEARCH_SUCCESS",
                                "搜尋景點成功",
                                placeService.convertToSearchResponse(
                                                places));
        }

        @RequirePermission("PLACE_SEARCH_NAME")
        @GetMapping("/search-name")
        public ApiResponse<List<Place>> searchName(
                        @RequestParam String keyword) {

                return ApiResponse.success(
                                "PLACE_SEARCH_NAME_SUCCESS",
                                "搜尋景點成功",
                                placeService.searchByKeyword(keyword));
        }

        @RequirePermission("PLACE_TYPE_LIST")
        @GetMapping("/types")
        public ApiResponse<List<String>> getPlaceTypes() {

                return ApiResponse.success(
                                "PLACE_TYPE_LIST_SUCCESS",
                                "取得景點類型成功",
                                List.of(
                                                "CAFE",
                                                "RESTAURANT",
                                                "HOTEL",
                                                "PARK",
                                                "HOSPITAL"));
        }
}
