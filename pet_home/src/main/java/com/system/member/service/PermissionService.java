package com.system.member.service;

import com.system.member.repository.PermissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PermissionService {

    @Autowired
    private PermissionRepository permissionRepository;

    @Transactional(readOnly = true)
    public boolean hasPermission(Long userId, String permissionCode) {
        List<String> permissions = permissionRepository.findPermissionCodesByUserId(userId);
        return permissions.contains(permissionCode);
    }
}
