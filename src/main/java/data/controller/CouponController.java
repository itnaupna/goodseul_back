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
@RequestMapping("/api")
public class CouponController {
    private final CouponService couponService;
    public CouponController(CouponService couponService) {
        this.couponService = couponService;
    }

    @GetMapping("/coupon")
    public ResponseEntity<Map<String, Object>> getPageCoupons (
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "createDate") String sortProperty,
            @RequestParam(defaultValue = "DESC") String sortDirection,
            @RequestParam(required = false) String keyword) {
        return new ResponseEntity<>(couponService.getPageCoupons(page, size, sortProperty, sortDirection, keyword), HttpStatus.OK);
    }

    @PostMapping("/coupon")
    public ResponseEntity<CouponDto> insertCoupon(@RequestBody CouponDto dto) {
        return new ResponseEntity<>(couponService.insertCoupon(dto), HttpStatus.OK);
    }

    @PutMapping("/coupon/{cp_id}")
    public ResponseEntity<CouponDto> updateReview(@PathVariable int cp_id, @RequestBody CouponDto dto){
        couponService.updateCoupon(cp_id, dto);
        return new ResponseEntity<CouponDto>(HttpStatus.OK);
    }


}
