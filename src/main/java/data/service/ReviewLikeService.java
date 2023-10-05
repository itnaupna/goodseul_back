package data.service;

import data.dto.ReviewLikeDto;
import data.entity.ReviewEntity;
import data.entity.ReviewLikeEntity;
import data.entity.UserEntity;
import data.repository.ReviewLikeRepository;
import data.repository.ReviewRepository;
import data.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class ReviewLikeService {
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final ReviewLikeRepository reviewLikeRepository;

    @Autowired
    public ReviewLikeService(ReviewRepository reviewRepository, ReviewLikeRepository reviewLikeRepository, UserRepository userRepository) {
        this.reviewRepository = reviewRepository;
        this.reviewLikeRepository = reviewLikeRepository;
        this.userRepository = userRepository;
    }

    public ResponseEntity<Object> insertLike (ReviewLikeDto dto) {
        ReviewEntity review = reviewRepository.findById(dto.getR_idx()).orElse(null);
        UserEntity user = userRepository.findById(dto.getU_idx()).orElse(null);

        log.info(review.toString() + user.toString());

        if(user == null || review == null) {
            throw  new RuntimeException("유저나 리뷰를 찾을 수 없어요");
        }

        if (reviewLikeRepository.existsByReviewEntity_rIdxAndUserEntity_idx(dto.getR_idx(), dto.getU_idx())) {
            return new ResponseEntity<>("이미 좋아요한 게시글입니다", HttpStatus.BAD_REQUEST);
        } else {
            ReviewLikeEntity like = ReviewLikeEntity.toReviewLikeEntity(dto, review, user);
            reviewLikeRepository.save(like);
            return new ResponseEntity<>(dto, HttpStatus.OK);
        }
    }

    public ResponseEntity<Object> deleteLike(Integer rIdx, Long uIdx) {
        Optional<ReviewLikeEntity> optionalLike = reviewLikeRepository.findByReviewEntity_rIdxAndUserEntity_idx(rIdx, uIdx);
        if (optionalLike.isPresent()) {
            reviewLikeRepository.delete(optionalLike.get());
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 404
        }
    }


}
