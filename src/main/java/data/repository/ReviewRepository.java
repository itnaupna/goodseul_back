package data.repository;

import data.entity.ReviewEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository <ReviewEntity, Integer> {
    Page<ReviewEntity> findByGoodseulEntity_GoodseulNameContainingOrUserEntity_NameContaining(String goodseul, String name, Pageable pageable);

    @Query("SELECT r, COUNT(rl) FROM review r LEFT JOIN review_like rl ON r.rIdx = rl.reviewEntity.rIdx GROUP BY r ORDER BY COUNT(rl) DESC")
    List<Object[]> findTopReviewsWithLikeCount(Pageable pageable);

    @Query("SELECT r, COUNT(rl) FROM review r LEFT JOIN review_like rl ON r.rIdx = rl.reviewEntity.rIdx " +
            "LEFT JOIN GoodseulEntity g ON r.goodseulEntity.idx = g.idx " +
            "WHERE g.isPremium > 0 GROUP BY r ORDER BY FUNCTION('RAND') ")
    List<Object[]> findRandomPremiumReviews(Pageable pageable);
    @Query(value = "SELECT GROUP_CONCAT(review.r_subject ORDER BY review.likeCount DESC) " +
            "FROM ( SELECT r.r_subject, COUNT(rl.r_idx) AS likeCount " +
            "    FROM review r LEFT JOIN review_like rl ON r.r_idx = rl.r_idx WHERE r.g_idx = :gIdx GROUP BY r.r_idx ORDER BY likeCount DESC " +
            "    LIMIT 5 ) AS review", nativeQuery = true)
    String findRandomReviewSubjectsByGIdx(@Param("gIdx") Long g_idx);
    Page<ReviewEntity> findByUserEntity_Idx(Long u_idx, Pageable pageable);

    int countAllByUserEntity_Idx(long uIdx);

}


