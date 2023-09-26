package data.service;

import data.dto.UserCouponDto;
import data.entity.CouponEntity;
import data.entity.UserCouponEntity;
import data.repository.CouponRepository;
import data.repository.UserCouponRepository;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@Slf4j
public class UserCouponService {

    private final Logger logger = LoggerFactory.getLogger(CouponService.class);

    @Autowired
    UserCouponRepository userCouponRepository;
    @Autowired
    CouponRepository couponRepository;

    public UserCouponService(UserCouponRepository userCouponRepository) {
        this.userCouponRepository = userCouponRepository;
    }

    //랜덤 쿠폰 번호 생성 (대문자+숫자 16자리)
    public String createCouponCode() {
        Random rnd = new Random();
        String code;
        int codeLength = 16;

        do {
            StringBuffer buf = new StringBuffer();
            for (int i = 1; i <= codeLength; i++) {
                if (rnd.nextBoolean())
                    buf.append((char) (rnd.nextInt(26) + 65)); // A-Z
                else
                    buf.append(rnd.nextInt(10)); // 0-9
            }
            code = buf.toString();
        } while (checkCouponCode(code));

        return code;
    }

    // 쿠폰 번호 중복 확인
    public boolean checkCouponCode(String code) {
        return userCouponRepository.existsByCpNumber(code);
    }

    public UserCouponDto insertUserCoupon(UserCouponDto dto) {
        // 쿠폰 코드 생성
        String code = createCouponCode();
        dto.setCp_number(code);

        // 쿠폰 존재 여부 조회
        CouponEntity couponEntity = couponRepository.findById(dto.getCp_idx()).orElse(null);
        if(couponEntity == null) {
            throw new RuntimeException("Coupon not found for ID: " + dto.getCp_idx());
        }

        UserCouponEntity ucoupon = UserCouponEntity.toUserCouponEntity(dto, couponEntity);
        userCouponRepository.save(ucoupon);

        return dto;
    }

}
