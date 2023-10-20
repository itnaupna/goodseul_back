package data.controller;

import data.dto.CouponDto;
import data.entity.CouponEntity;
import data.service.CouponService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("/api/lv1/coupon")
@Api(value = "쿠폰 등록", description = "Coupon Controller", tags = "쿠폰 API")
public class CouponController {
    private final CouponService couponService;
    public CouponController(CouponService couponService) {
        this.couponService = couponService;
    }

    // 사용 안됨 / 삭제할지 검토해야함
    @ApiOperation(value = "모든 쿠폰 조회", notes = "현재 사용되지 않음")
    @GetMapping("/all")
    public ResponseEntity<Map<String, Object>> getPageCoupons (
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "cpIdx") String sortProperty,
            @RequestParam(defaultValue = "DESC") String sortDirection,
            @RequestParam(required = false) String keyword
            ) {
        return new ResponseEntity<>(couponService.getPageCoupons(page, size, sortProperty, sortDirection, keyword), HttpStatus.OK);
    }
    @ApiOperation(value = "구매 가능한 쿠폰 조회", notes = "상점에서 구매 가능한 쿠폰 리스트")
    @GetMapping
    public ResponseEntity<Map<String, Object>> getBuyableCoupons (
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "cpIdx") String sortProperty,
            @RequestParam(defaultValue = "DESC") String sortDirection
    ) {
        return new ResponseEntity<>(couponService.getBuyableCoupons(page, size, sortProperty, sortDirection), HttpStatus.OK);
    }
    @ApiOperation(value = "쿠폰 추가", notes = "새로운 쿠폰 생성")
    @PostMapping
    public ResponseEntity<CouponEntity> insertCoupon(CouponDto dto, MultipartFile upload) {
        try {
            return new ResponseEntity<>(couponService.insertCoupon(dto, upload), HttpStatus.OK);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @ApiOperation(value = "쿠폰 수정", notes = "쿠폰 정보를 수정")
    @ApiImplicitParam(name = "cp_idx", value = "쿠폰 아이디", dataType = "int", required = true, paramType = "path")
    @PutMapping("/{cp_idx}")
    public ResponseEntity<CouponDto> updateReview(@PathVariable int cp_idx, CouponDto dto, MultipartFile upload) throws IOException {
        couponService.updateCoupon(cp_idx, dto, upload);
        return new ResponseEntity<CouponDto>(HttpStatus.OK);
    }


}
