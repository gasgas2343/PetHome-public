package com.system.petpost.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.system.member.dto.ApiResponse;
import com.system.petpost.service.PetpostPermissionService;
import com.system.petpost.util.LoginUserUtil;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/petpost/permissions")
@RequiredArgsConstructor
public class PetpostPermissionController {
    private final PetpostPermissionService petpostPermissionService;
    @GetMapping("/me")
    public ApiResponse<List<String>> findMyPermissions() {
        Long userId = LoginUserUtil.getLoginUserId();
        return ApiResponse.success(
                "PERMISSION_SUCCESS",
                "取得權限成功",
                petpostPermissionService.findPermissionCodesByUserId(userId));
    }
}