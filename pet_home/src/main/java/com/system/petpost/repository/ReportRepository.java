package com.system.petpost.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.system.petpost.entity.Report;

public interface ReportRepository
                extends JpaRepository<Report, Long> {

        // 查詢檢舉人所有檢舉紀錄
        List<Report> findByReporterId(
                        Long reporterId);

        // 查詢被檢舉人所有檢舉紀錄
        List<Report> findByReportedUserId(
                        Long reportedUserId);

        // 查詢指定狀態檢舉
        List<Report> findByStatus(
                        Byte status);

        // 查詢指定文章檢舉
        List<Report> findByPostId(
                        Long postId);

        // 查詢指定留言檢舉
        List<Report> findByCommentId(
                        Long commentId);

        // 是否已檢舉過該文章
        boolean existsByReporterIdAndPostId(
                        Long reporterId,
                        Long postId);

        // 是否已檢舉過該留言
        boolean existsByReporterIdAndCommentId(
                        Long reporterId,
                        Long commentId);

        // 中文註解：統計待審核檢舉數。
        long countByStatus(Byte status);

        // 中文註解：統計某會員被檢舉次數。
        long countByReportedUserId(Long reportedUserId);

        // 中文註解：檢查文章是否有檢舉成立紀錄。
        boolean existsByPostIdAndStatus(Long postId, Byte status);
}