package com.system.petpost.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.system.member.repository.PermissionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PetpostPermissionService {
    private final PermissionRepository permissionRepository;
    public List<String> findPermissionCodesByUserId(Long userId) {
        return permissionRepository.findPermissionCodesByUserId(userId);
    }
}