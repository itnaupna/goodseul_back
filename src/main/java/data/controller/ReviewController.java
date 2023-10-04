package data.controller;


import data.dto.ReviewDto;
import data.service.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/lv0/review")
    public ResponseEntity<ReviewDto> insertReview (@RequestBody ReviewDto dto) {
        log.info(dto.toString());
        return new ResponseEntity<>(reviewService.insertReview(dto), HttpStatus.OK);
    }



}
