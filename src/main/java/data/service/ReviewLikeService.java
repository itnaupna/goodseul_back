package data.service;

import data.dto.ReviewLikeDto;
import data.entity.ReviewEntity;
import data.entity.ReviewLikeEntity;
import data.entity.UserEntity;
import data.exception.AlreadyProcessException;
import data.exception.UserNotFoundException;
import data.repository.ReviewLikeRepository;
import data.repository.ReviewRepository;
import data.repository.UserRepository;
import jwt.setting.settings.JwtService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@Service
@Slf4j
public class ReviewLikeService {
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final ReviewLikeRepository reviewLikeRepository;
    private final JwtService jwtService;

    @Autowired
    public ReviewLikeService(ReviewRepository reviewRepository, ReviewLikeRepository reviewLikeRepository, UserRepository userRepository, JwtService jwtService) {
        this.reviewRepository = reviewRepository;
        this.reviewLikeRepository = reviewLikeRepository;
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    public void insertLike (ReviewLikeDto dto, HttpServletRequest request) {
        long idx = jwtService.extractIdxFromRequest(request);
        dto.setU_idx(idx);
        ReviewEntity review = reviewRepository.findById(dto.getR_idx()).orElseThrow(()-> new EntityNotFoundException("해당 리뷰를 찾을 수 없습니다"));
        UserEntity user = userRepository.findById(dto.getU_idx()).orElseThrow((UserNotFoundException::new));

        log.info(review.toString() + user.toString());

        if (reviewLikeRepository.existsByReviewEntity_rIdxAndUserEntity_idx(dto.getR_idx(), dto.getU_idx())) {
            throw new AlreadyProcessException();
        } else {
            ReviewLikeEntity like = ReviewLikeEntity.toReviewLikeEntity(dto, review, user);
            reviewLikeRepository.save(like);
        }
    }

    public void deleteLike (@PathVariable  int r_idx, HttpServletRequest request) {
        long idx = jwtService.extractIdxFromRequest(request);
        ReviewLikeEntity optionalLike = reviewLikeRepository.findByReviewEntity_rIdxAndUserEntity_idx(r_idx, idx).orElseThrow(EntityNotFoundException::new);
        reviewLikeRepository.delete(optionalLike);
    }

}
