package data.controller;

import data.dto.ReviewLikeDto;
import data.dto.ReviewResponseDto;
import data.service.ReviewLikeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@Slf4j
@RequestMapping("/api")
public class ReviewLikeController {

    private ReviewLikeService reviewLikeService;

    public ReviewLikeController(ReviewLikeService reviewLikeService) {
        this.reviewLikeService = reviewLikeService;
    }

    @PostMapping("/lv0/like")
    public ResponseEntity<Object> insertLike(@RequestBody ReviewLikeDto dto) {
        log.info(dto.toString());
        return reviewLikeService.insertLike(dto);
    }

    @DeleteMapping("/lv0/like")
    public ResponseEntity<Object> deleteLike(@RequestParam int rIdx, @RequestParam Long uIdx) {
        return  reviewLikeService.deleteLike(rIdx, uIdx);
    }

}
