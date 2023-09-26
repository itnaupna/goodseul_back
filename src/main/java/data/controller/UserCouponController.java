package data.controller;


import data.dto.UserCouponDto;
import data.service.UserCouponService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class UserCouponController {

    private final UserCouponService userCouponService;
    public UserCouponController(UserCouponService userCouponService) {
        this.userCouponService = userCouponService;
    }

    @PostMapping("/usercoupon")
    public ResponseEntity<UserCouponDto> insertCoupon(@RequestBody UserCouponDto dto) {
        return new ResponseEntity<>(userCouponService.insertUserCoupon(dto), HttpStatus.OK);
    }

}
