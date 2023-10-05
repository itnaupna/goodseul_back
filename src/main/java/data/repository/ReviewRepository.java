package data.repository;

import data.dto.ReviewResponseDto;
import data.entity.ReviewEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository <ReviewEntity, Integer> {
    Page<ReviewEntity> findByGoodseulEntity_GoodseulNameContainingOrUserEntity_NameContaining(String goodseul, String name, Pageable pageable);

    @Query("SELECT r, COUNT(rl) FROM review r LEFT JOIN review_like rl ON r.rIdx = rl.reviewEntity.rIdx GROUP BY r ORDER BY COUNT(rl) DESC")
    List<Object[]> findTopReviewsWithLikeCount(Pageable pageable);

}


