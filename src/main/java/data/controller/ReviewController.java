package data.controller;


import data.dto.ReviewDto;
import data.dto.ReviewResponseDto;
import data.service.ReviewService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
@Slf4j
@RequestMapping("/api")
@Api(value = "리뷰 관리", description = "Review Controller", tags = "리뷰 API")
public class ReviewController {

    private final ReviewService reviewService;
    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @ApiOperation(value = "전체 리뷰 리스트")
    @GetMapping("/lv0/review")
    public ResponseEntity<Map<String, Object>> getPageReviews (
            @ApiParam(value = "페이지 번호", defaultValue = "0") @RequestParam(defaultValue = "0") int page,
            @ApiParam(value = "페이지 당 출력될 게시글 수", defaultValue = "6") @RequestParam(defaultValue = "6") int size,
            @ApiParam(value = "정렬 기준", defaultValue = "rCreateDate") @RequestParam(defaultValue = "rCreateDate") String sortProperty,
            @ApiParam(value = "정렬 순서", defaultValue = "DESC") @RequestParam(defaultValue = "DESC") String sortDirection,
            @ApiParam(value = "검색 키워드", required = false) @RequestParam(required = false) String keyword) {
        return new ResponseEntity<>(reviewService.getPageReview(page, size, sortProperty, sortDirection, keyword), HttpStatus.OK);
    }
    @ApiOperation(value = "마이페이지 내 리뷰 출력")
    @GetMapping("/lv1/mypage/review")
    public ResponseEntity<Map<String, Object>> getPageMyReview (
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "rCreateDate") String sortProperty,
            @RequestParam(defaultValue = "DESC") String sortDirection,
            HttpServletRequest request) {
        return new ResponseEntity<>(reviewService.getPageMyReview(page, size, sortProperty, sortDirection, request), HttpStatus.OK);
    }
    @ApiOperation(value = "리뷰 디테일 페이지")
    @GetMapping("/lv0/review/{r_idx}")
    public ResponseEntity<ReviewResponseDto>  getOneReview (@ApiParam(value = "리뷰 인덱스") @PathVariable int r_idx) {
        try {
            ReviewResponseDto review = reviewService.getOneReview(r_idx);
            return new ResponseEntity<>(review, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    @ApiOperation(value = "리뷰 추가")
    @PostMapping("/lv1/review")
    public ResponseEntity<ReviewDto> insertReview (@ApiParam(value = "리뷰 DTO 정보") @RequestBody ReviewDto dto, HttpServletRequest request) {
        log.info(dto.toString());
        return new ResponseEntity<>(reviewService.insertReview(dto, request), HttpStatus.OK);
    }
    @ApiOperation(value = "베스트 리뷰(좋아요순) 리스트")
    @GetMapping("/lv0/review/best")
    public ResponseEntity<List<ReviewResponseDto>> getTopReviews() {
        return new ResponseEntity<>(reviewService.findTopReviews(), HttpStatus.OK);
    }
    @ApiOperation(value = "프리미엄 리뷰 (랜덤으로 isPremium 인 구슬 리뷰만 출력)")
    @GetMapping("/lv0/review/premium")
    public ResponseEntity<List<ReviewResponseDto>> findRandomPremiumReviews() {
        return new ResponseEntity<>(reviewService.findRandomPremiumReviews(), HttpStatus.OK);
    }
    @ApiOperation(value = "리뷰 삭제")
    @DeleteMapping("/lv1/review/{r_idx}")
    public ResponseEntity<Object> deleteReview (@ApiParam(value = "리뷰 인덱스") @PathVariable int r_idx) {
        boolean result = reviewService.deleteReview(r_idx);
        if(result) {
            return new ResponseEntity<>("삭제 완료", HttpStatus.OK);
        }else {
            return new ResponseEntity<>("존재 하지 않는 리뷰 입니다", HttpStatus.BAD_REQUEST);
        }
    }
    @ApiOperation(value = "리뷰 수정")
    @PutMapping("/lv1/review/{r_idx}")
    public ResponseEntity<Object> updateReview ( @ApiParam(value = "리뷰 인덱스") @PathVariable int r_idx,
                                                 @ApiParam(value = "리뷰 DTO 정보") @RequestBody ReviewDto dto) {
        boolean result = reviewService.updateReview(r_idx, dto);
        if(result) {
            return new ResponseEntity<>("업데이트 완료", HttpStatus.OK);
        }else {
            return new ResponseEntity<>("업데이트 실패", HttpStatus.BAD_REQUEST);
        }

    }

}
