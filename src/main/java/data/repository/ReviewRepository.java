package data.repository;

import data.dto.ReviewResponseDto;
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

//    @Query(value = "SELECT GROUP_CONCAT(r.r_subject) FROM (SELECT r_subject FROM review WHERE g_idx = :gIdx ORDER BY RAND() LIMIT 5) r",
//            nativeQuery = true)
//    String findRandomReviewSubjectsByGIdx(@Param("gIdx") Long gIdx);

    @Query(value = "SELECT GROUP_CONCAT(review.r_subject ORDER BY review.likeCount DESC) " +
            "FROM ( SELECT r.r_subject, COUNT(rl.r_idx) AS likeCount " +
            "    FROM review r LEFT JOIN review_like rl ON r.r_idx = rl.r_idx WHERE r.g_idx = :gIdx GROUP BY r.r_idx ORDER BY likeCount DESC " +
            "    LIMIT 5 ) AS review", nativeQuery = true)
    String findRandomReviewSubjectsByGIdx(@Param("gIdx") Long gIdx);

}


