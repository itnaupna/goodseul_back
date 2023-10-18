package data.controller;


import data.dto.UserCouponDto;
import data.service.UserCouponService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("/api/lv1/usercoupon")
public class UserCouponController {

    private final UserCouponService userCouponService;
    public UserCouponController(UserCouponService userCouponService) {
        this.userCouponService = userCouponService;
    }

    @PostMapping
    public ResponseEntity<UserCouponDto> insertCoupon(@RequestBody UserCouponDto dto, HttpServletRequest request) {
        return new ResponseEntity<>(userCouponService.insertUserCoupon(dto, request), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getPageMyReview (
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "ucpCreateDate") String sortProperty,
            @RequestParam(defaultValue = "DESC") String sortDirection,
            HttpServletRequest request) {
        return new ResponseEntity<>(userCouponService.getPageMyCoupon(page, size, sortProperty, sortDirection, request), HttpStatus.OK);
    }

}
