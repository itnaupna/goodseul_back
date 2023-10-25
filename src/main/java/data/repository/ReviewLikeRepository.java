package data.repository;

import data.entity.ReviewLikeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewLikeRepository extends JpaRepository <ReviewLikeEntity, Integer> {

    boolean existsByReviewEntity_rIdxAndUserEntity_idx(int rIdx, Long uIdx);
    Optional<ReviewLikeEntity> findByReviewEntity_rIdxAndUserEntity_idx(int rIdx, Long uIdx);
    Integer countReviewLikeEntitiesByReviewEntity_rIdx(int rIdx);

}
