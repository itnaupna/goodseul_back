package data.controller;


import data.dto.UserCouponDto;
import data.service.UserCouponService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("/api/lv1/usercoupon")
@Api(value = "유저 쿠폰 관리", description = "UserCoupon Controller", tags = "유저 쿠폰 API")
public class UserCouponController {

    private final UserCouponService userCouponService;
    public UserCouponController(UserCouponService userCouponService) {
        this.userCouponService = userCouponService;
    }

    @ApiOperation(value = "사용자 쿠폰 추가")
    @PostMapping
    public ResponseEntity<Boolean> insertCoupon(@RequestBody @ApiParam(value = "사용자 쿠폰 정보") UserCouponDto dto, HttpServletRequest request) {
        return new ResponseEntity<>(userCouponService.insertUserCoupon(dto, request), HttpStatus.OK);
    }

    @ApiOperation(value = "마이페이지 내 쿠폰 조회")
    @GetMapping
    public ResponseEntity<Map<String, Object>> getPageMyReview (
            @RequestParam(defaultValue = "0") @ApiParam(value = "페이지 번호") int page,
            @RequestParam(defaultValue = "5") @ApiParam(value = "페이지 크기") int size,
            @RequestParam(defaultValue = "ucpCreateDate") @ApiParam(value = "정렬 속성") String sortProperty,
            @RequestParam(defaultValue = "DESC") @ApiParam(value = "정렬 방향") String sortDirection,
            HttpServletRequest request) {
        return new ResponseEntity<>(userCouponService.getPageMyCoupon(page, size, sortProperty, sortDirection, request), HttpStatus.OK);
    }

}
