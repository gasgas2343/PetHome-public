package com.system.petpost.service;

import java.util.List;

import com.system.petpost.dto.AppealCreateDTO;
import com.system.petpost.dto.admin.AdminAppealDTO;

import com.system.petpost.entity.Appeal;

/**
 * 申訴 Service 介面
 *
 * 中文註解：
 * Controller 只依賴介面，真正業務邏輯放在 AppealServiceImpl。
 */
public interface AppealService {
    
    List<AdminAppealDTO> findPendingAppealDTOs();
    /**
     * 中文註解：建立申訴。
     */
    Appeal create(AppealCreateDTO dto);

    /**
     * 中文註解：查詢所有申訴。
     */
    List<Appeal> findAll();

    /**
     * 中文註解：查詢單筆申訴。
     */
    Appeal findById(Long appealId);

    /**
     * 中文註解：查詢待審核申訴。
     */
    List<Appeal> findPendingAppeals();

    /**
     * 中文註解：管理員審核申訴通過。
     */
    Appeal approve(Long appealId, Long adminId, String note);

    /**
     * 中文註解：管理員駁回申訴。
     */
    Appeal reject(Long appealId, Long adminId, String note);

    
}
