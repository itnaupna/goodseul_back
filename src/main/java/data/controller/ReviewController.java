package data.controller;


import data.dto.ReviewDto;
import data.dto.ReviewResponseDto;
import data.service.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
@Slf4j
@RequestMapping("/api")
public class ReviewController {

    private final ReviewService reviewService;
    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping("/lv0/review")
    public ResponseEntity<Map<String, Object>> getPageReviews (
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size,
            @RequestParam(defaultValue = "rCreateDate") String sortProperty,
            @RequestParam(defaultValue = "DESC") String sortDirection,
            @RequestParam(required = false) String keyword) {
        return new ResponseEntity<>(reviewService.getPageReview(page, size, sortProperty, sortDirection, keyword), HttpStatus.OK);
    }

    @GetMapping("/lv1/mypage/review/{u_idx}")
    public ResponseEntity<Map<String, Object>> getPageMyReview (
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "rCreateDate") String sortProperty,
            @RequestParam(defaultValue = "DESC") String sortDirection,
            @PathVariable Long u_idx) {
        return new ResponseEntity<>(reviewService.getPageMyReview(page, size, sortProperty, sortDirection, u_idx), HttpStatus.OK);
    }

    @GetMapping("/lv0/review/{r_idx}")
    public ResponseEntity<ReviewResponseDto>  getOneReview (@PathVariable int r_idx) {
        try {
            ReviewResponseDto review = reviewService.getOneReview(r_idx);
            return new ResponseEntity<>(review, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/lv1/review")
    public ResponseEntity<ReviewDto> insertReview (@RequestBody ReviewDto dto) {
        log.info(dto.toString());
        return new ResponseEntity<>(reviewService.insertReview(dto), HttpStatus.OK);
    }

    @GetMapping("/lv0/review/best")
    public ResponseEntity<List<ReviewResponseDto>> getTopReviews() {
        return new ResponseEntity<>(reviewService.findTopReviews(), HttpStatus.OK);
    }

    @GetMapping("/lv0/review/premium")
    public ResponseEntity<List<ReviewResponseDto>> findRandomPremiumReviews() {
        return new ResponseEntity<>(reviewService.findRandomPremiumReviews(), HttpStatus.OK);
    }

    @DeleteMapping("/lv1/review/{r_idx}")
    public ResponseEntity<Object> deleteReview (@PathVariable int r_idx) {
        boolean result = reviewService.deleteReview(r_idx);
        if(result) {
            return new ResponseEntity<>("삭제 완료", HttpStatus.OK);
        }else {
            return new ResponseEntity<>("존재 하지 않는 리뷰 입니다", HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/lv1/review/{r_idx}")
    public ResponseEntity<Object> updateReview (@PathVariable int r_idx, @RequestBody ReviewDto dto) {
        boolean result = reviewService.updateReview(r_idx, dto);
        if(result) {
            return new ResponseEntity<>("업데이트 완료", HttpStatus.OK);
        }else {
            return new ResponseEntity<>("업데이트 실패", HttpStatus.BAD_REQUEST);
        }

    }


}
