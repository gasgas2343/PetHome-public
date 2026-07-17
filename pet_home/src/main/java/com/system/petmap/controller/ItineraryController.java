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

import com.system.petmap.dto.ItineraryPlaceReorderRequest;
import com.system.member.dto.ApiResponse;
import com.system.member.security.annotation.RequirePermission;
import com.system.petmap.dto.ItineraryCreateRequest;
import com.system.petmap.dto.ItineraryDetailResponse;
import com.system.petmap.dto.ItineraryPlaceCreateRequest;
import com.system.petmap.dto.ItineraryUpdateRequest;
import com.system.petmap.entity.Itinerary;
import com.system.petmap.entity.ItineraryPlace;
import com.system.petmap.service.ItineraryService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/map/itineraries")
@RequiredArgsConstructor
public class ItineraryController {

        private final ItineraryService itineraryService;

        @RequirePermission("ITINERARY_CREATE")
        @PostMapping
        public ApiResponse<Itinerary> create(
                        @Valid @RequestBody ItineraryCreateRequest request) {

                return ApiResponse.success(
                                "ITINERARY_CREATE_SUCCESS",
                                "建立行程成功",
                                itineraryService.create(request));
        }

        @RequirePermission("ITINERARY_USER_LIST")
        @GetMapping("/user/{userId}")
        public ApiResponse<List<Itinerary>> findByUserId(
                        @PathVariable Long userId) {

                return ApiResponse.success(
                                "ITINERARY_LIST_SUCCESS",
                                "取得行程成功",
                                itineraryService.findByUserId(userId));
        }

        @RequirePermission("ITINERARY_PLACE_ADD")
        @PostMapping("/{itineraryId}/places")
        public ApiResponse<ItineraryPlace> addPlace(
                        @PathVariable Integer itineraryId,
                        @Valid @RequestBody ItineraryPlaceCreateRequest request) {

                return ApiResponse.success(
                                "ITINERARY_PLACE_CREATE_SUCCESS",
                                "新增景點成功",
                                itineraryService.addPlace(
                                                itineraryId,
                                                request));
        }

        @RequirePermission("ITINERARY_DETAIL")
        @GetMapping("/{itineraryId}")
        public ApiResponse<ItineraryDetailResponse> getDetail(
                        @PathVariable Integer itineraryId) {

                return ApiResponse.success(
                                "ITINERARY_DETAIL_SUCCESS",
                                "取得行程成功",
                                itineraryService.getDetail(itineraryId));
        }

        @RequirePermission("ITINERARY_UPDATE")
        @PutMapping("/{itineraryId}")
        public ApiResponse<Itinerary> update(
                        @PathVariable Integer itineraryId,
                        @Valid @RequestBody ItineraryUpdateRequest request) {

                return ApiResponse.success(
                                "ITINERARY_UPDATE_SUCCESS",
                                "更新行程成功",
                                itineraryService.update(
                                                itineraryId,
                                                request));
        }

        @RequirePermission("ITINERARY_DELETE")
        @DeleteMapping("/{itineraryId}")
        public ApiResponse<Void> delete(
                        @PathVariable Integer itineraryId) {

                itineraryService.delete(itineraryId);

                return ApiResponse.success(
                                "ITINERARY_DELETE_SUCCESS",
                                "刪除行程成功",
                                null);
        }

        @RequirePermission("ITINERARY_PLACE_REMOVE")
        @DeleteMapping("/{itineraryId}/places/{placeId}")
        public ApiResponse<Void> removePlace(
                        @PathVariable Integer itineraryId,
                        @PathVariable Integer placeId) {

                itineraryService.removePlace(
                                itineraryId,
                                placeId);

                return ApiResponse.success(
                                "ITINERARY_PLACE_DELETE_SUCCESS",
                                "移除景點成功",
                                null);
        }

        @RequirePermission("ITINERARY_REORDER")
        @PutMapping("/{itineraryId}/reorder")
        public ApiResponse<Void> reorder(
                        @PathVariable Integer itineraryId,
                        @RequestBody List<ItineraryPlaceReorderRequest> request) {

                itineraryService.reorder(
                                itineraryId,
                                request);

                return ApiResponse.success(
                                "ITINERARY_REORDER_SUCCESS",
                                "排序成功",
                                null);
        }
}