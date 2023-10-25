package data.service;

import data.dto.ReviewDto;
import data.dto.ReviewResponseDto;
import data.entity.GoodseulEntity;
import data.entity.ReviewEntity;
import data.entity.UserEntity;
import data.exception.GoodseulNotFoundException;
import data.exception.UserNotFoundException;
import data.repository.GoodseulRepository;
import data.repository.ReviewLikeRepository;
import data.repository.ReviewRepository;
import data.repository.UserRepository;
import jwt.setting.settings.JwtService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final GoodseulRepository goodseulRepository;
    private final UserRepository userRepository;
    private final ReviewLikeRepository reviewLikeRepository;
    private final JwtService jwtService;

    public ReviewService(ReviewRepository reviewRepository, GoodseulRepository goodseulRepository, UserRepository userRepository, ReviewLikeRepository reviewLikeRepository, JwtService jwtService) {
        this.reviewRepository = reviewRepository;
        this.goodseulRepository = goodseulRepository;
        this.userRepository = userRepository;
        this.reviewLikeRepository = reviewLikeRepository;
        this.jwtService = jwtService;
    }

    public Map<String, Object> getPageReview(int page, int size, String sortProperty, String sortDirection, String keyword) {
        Sort.Direction direction = Sort.Direction.fromString(sortDirection);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortProperty));
        Page<ReviewEntity> result;

        if (keyword != null && !keyword.trim().isEmpty()) {
            result = reviewRepository.findByGoodseulEntity_GoodseulNameContainingOrUserEntity_NameContaining(keyword, keyword, pageable);
        } else if (keyword == null) {
            result = reviewRepository.findAll(pageable);
        } else {
            result = new PageImpl<>(Collections.emptyList());
        }

        List<ReviewResponseDto> reviewDtos = result.getContent().stream().map(review -> {
            Integer likeCount = reviewLikeRepository.countReviewLikeEntitiesByReviewEntity_rIdx(review.getRIdx());
            return new ReviewResponseDto(review, likeCount);
        }).collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("reviews", reviewDtos);
        response.put("totalElements", result.getTotalElements());
        response.put("totalPages", result.getTotalPages());
        response.put("currentPage", result.getNumber() + 1);
        response.put("hasNext", result.hasNext());

        return response;
    }

    public Map<String, Object> getPageMyReview(int page, int size, String sortProperty, String sortDirection, HttpServletRequest request) {
        long idx = jwtService.extractIdxFromRequest(request);
        Sort.Direction direction = Sort.Direction.fromString(sortDirection);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortProperty));
        Page<ReviewEntity> result = reviewRepository.findByUserEntity_Idx(idx, pageable);

        List<ReviewResponseDto> reviewDtos = result.getContent().stream().map(review -> {
            Integer likeCount = reviewLikeRepository.countReviewLikeEntitiesByReviewEntity_rIdx(review.getRIdx());
            return new ReviewResponseDto(review, likeCount);
        }).collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("reviews", reviewDtos);
        response.put("totalElements", result.getTotalElements());
        response.put("totalPages", result.getTotalPages());
        response.put("currentPage", result.getNumber() + 1);
        response.put("hasNext", result.hasNext());

        return response;
    }

    public ReviewResponseDto getOneReview(int r_idx, HttpServletRequest request) {
        long idx;
        boolean likeStatus;

        try {
            idx = jwtService.extractIdxFromRequest(request);
            likeStatus = reviewLikeRepository.countByReviewEntity_rIdxAndUserEntity_idx(r_idx, idx) > 0;
        } catch (Exception e) {
            idx = 0;
            likeStatus = false;
        }
        ReviewEntity review = reviewRepository.findById(r_idx).orElseThrow(EntityNotFoundException::new);
        Integer likeCount = reviewLikeRepository.countReviewLikeEntitiesByReviewEntity_rIdx(review.getRIdx());

        ReviewResponseDto response = new ReviewResponseDto(review, likeCount);
        response.setLikeStatus(likeStatus);
        return response;
    }


    public ReviewDto insertReview(ReviewDto dto, HttpServletRequest request) {
        long idx = jwtService.extractIdxFromRequest(request);
        dto.setU_idx(idx);

        GoodseulEntity goodseul = goodseulRepository.findById(dto.getG_idx()).orElseThrow(GoodseulNotFoundException::new);
        UserEntity user = userRepository.findById(idx).orElseThrow(UserNotFoundException::new);

        ReviewEntity review = ReviewEntity.toReviewEntity(dto, goodseul, user);
        reviewRepository.save(review);

        return dto;
    }

    public List<ReviewResponseDto> findTopReviews() {
        Pageable topFive = PageRequest.of(0, 5);
        List<Object[]> results = reviewRepository.findTopReviewsWithLikeCount(topFive);

        return results.stream().map(result -> new ReviewResponseDto((ReviewEntity) result[0], ((Long) result[1]).intValue())).collect(Collectors.toList());
    }

    public List<ReviewResponseDto> findRandomPremiumReviews() {
        Pageable topFiveRandom = PageRequest.of(0, 5);
        List<Object[]> reults = reviewRepository.findRandomPremiumReviews(topFiveRandom);
        return reults.stream()
                .filter(result -> result[0] != null)
                .map(result -> {
                    ReviewEntity reviewEntity = (ReviewEntity) result[0];
                    Integer likeCount = ((Long) result[1]).intValue();

                    ReviewResponseDto responseDto = new ReviewResponseDto(reviewEntity, likeCount);

                    String randSubject = reviewRepository.findRandomReviewSubjectsByGIdx(reviewEntity.getGoodseulEntity().getIdx());
                    responseDto.setRandSubject(randSubject);

                    return responseDto;
                }).collect(Collectors.toList());
    }

    public boolean deleteReview(int r_idx) {
        boolean reult = reviewRepository.existsById(r_idx);
        if (reult) {
            reviewRepository.deleteById(r_idx);
        }
        return reult;
    }

    public boolean updateReview(int r_idx, ReviewDto dto) {
        try {
            Optional<ReviewEntity> entity = reviewRepository.findById(r_idx);

            if (entity.isPresent()) {
                ReviewEntity review = entity.get();
                review.setRSubject(dto.getR_subject());
                review.setRContent(dto.getR_content());
                review.setRType(dto.getR_type());
                review.setStar(dto.getStar());
                reviewRepository.save(review);
                return true;
            }

        } catch (Exception e) {
            log.info("review update error" + e.getMessage());
            return false;
        }
        return false;
    }


}
