package data.controller;

import data.dto.CouponDto;
import data.service.CouponService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("/api/lv0/coupon")
public class CouponController {
    private final CouponService couponService;
    public CouponController(CouponService couponService) {
        this.couponService = couponService;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getPageCoupons (
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "createDate") String sortProperty,
            @RequestParam(defaultValue = "DESC") String sortDirection,
            @RequestParam(required = false) String keyword
            ) {
        return new ResponseEntity<>(couponService.getPageCoupons(page, size, sortProperty, sortDirection, keyword), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<CouponDto> insertCoupon(@RequestBody CouponDto dto) {
        return new ResponseEntity<>(couponService.insertCoupon(dto), HttpStatus.OK);
    }

    @PutMapping("/{cp_idx}")
    public ResponseEntity<CouponDto> updateReview(@PathVariable int cp_idx, @RequestBody CouponDto dto){
        couponService.updateCoupon(cp_idx, dto);
        return new ResponseEntity<CouponDto>(HttpStatus.OK);
    }


}
