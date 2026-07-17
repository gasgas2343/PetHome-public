package com.system.petpost.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.system.petpost.entity.ForumPost;

public interface ForumPostRepository
                extends JpaRepository<ForumPost, Long> {

        // 依標題搜尋
        List<ForumPost> findByTitleContaining(
                        String keyword);

        // 查詢指定狀態文章
        List<ForumPost> findByStatus(
                        Byte status);

        // 查詢熱門文章
        List<ForumPost> findTop10ByOrderByTrendingScoreDesc();

        // 查詢正常文章(置頂優先)
        List<ForumPost> findByStatusOrderByIsPinnedDescCreatedAtDesc(
                        Byte status);

        // 查詢熱門正常文章
        List<ForumPost> findTop10ByStatusOrderByTrendingScoreDesc(
                        Byte status);

        // 中文註解：統計狀態為正常的文章數。
        // 如果你的 status 是 Byte / Integer，請依照 Entity 型別調整參數型別。
        long countByStatus(Byte status);

        long countByUserIdAndStatus(Long userId, Byte status);

        // 中文註解：前台文章列表要同時查正常文章與被隱藏文章。
        List<ForumPost> findByStatusInOrderByIsPinnedDescCreatedAtDesc(List<Byte> statuses);

        // 中文註解：熱門文章同時查正常文章與被隱藏文章。
        List<ForumPost> findTop10ByStatusInOrderByTrendingScoreDesc(List<Byte> statuses);

}
