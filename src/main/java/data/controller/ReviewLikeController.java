package data.controller;

import data.dto.ReviewLikeDto;
import data.dto.ReviewResponseDto;
import data.service.ReviewLikeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@CrossOrigin
@Slf4j
@Api(value = "리뷰 좋아요 관리", description = "ReviewLike Controller", tags = "리뷰 좋아요 API")
@RequestMapping("/api")
public class ReviewLikeController {

    private ReviewLikeService reviewLikeService;

    public ReviewLikeController(ReviewLikeService reviewLikeService) {
        this.reviewLikeService = reviewLikeService;
    }

    @ApiOperation(value = "리뷰 좋아요 추가")
    @PostMapping("/lv1/like")
    public ResponseEntity<String> insertLike(
            @ApiParam(value = "리뷰 좋아요 DTO 정보") @RequestBody ReviewLikeDto dto, HttpServletRequest request) {
        reviewLikeService.insertLike(dto, request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation(value = "리뷰 좋아요 삭제")
    @DeleteMapping("/lv1/like/{r_idx}")
    public ResponseEntity<Object> deleteLike(
            @ApiParam(value = "리뷰 idx") @PathVariable int r_idx,
            @ApiParam(value = "유저 idx") HttpServletRequest request) {
        reviewLikeService.deleteLike(r_idx, request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
