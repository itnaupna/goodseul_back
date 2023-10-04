package data.repository;

import data.entity.ReviewEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository <ReviewEntity, Integer> {
    Page<ReviewEntity> findByGoodseulEntity_GoodseulNameContainingOrUserEntity_NameContaining(String goodseul, String name, Pageable pageable);
}


