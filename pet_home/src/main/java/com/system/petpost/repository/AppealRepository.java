package com.system.petpost.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.system.petpost.entity.Appeal;

public interface AppealRepository
                extends JpaRepository<Appeal, Long> {

        // 查詢申訴人所有申訴
        List<Appeal> findByUserId(
                        Long userId);

        // 查詢指定狀態申訴
        List<Appeal> findByStatus(
                        Byte status);

        // 查詢指定檢舉的申訴
        List<Appeal> findByReportId(
                        Long reportId);

        // 中文註解：檢查指定檢舉案件是否已經建立申訴，避免重複申訴。
        boolean existsByReportId(
                        Long reportId);

        // 中文註解：統計待審核申訴數。
        long countByStatus(Byte status);

        boolean existsByUserIdAndTargetTypeAndTargetIdAndStatus(
                        Long userId,
                        String targetType,
                        Long targetId,
                        Byte status);

        

}