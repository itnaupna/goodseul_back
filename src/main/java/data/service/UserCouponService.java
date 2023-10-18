package data.service;

import data.dto.UserCouponDto;
import data.dto.UserCouponResponseDto;
import data.entity.CouponEntity;
import data.entity.UserCouponEntity;
import data.entity.UserEntity;
import data.repository.CouponRepository;
import data.repository.UserCouponRepository;
import data.repository.UserRepository;
import jwt.setting.settings.JwtService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserCouponService {
   private final UserCouponRepository userCouponRepository;
   private final CouponRepository couponRepository;
   private final UserRepository userRepository;
   private final JwtService jwtService;

    public UserCouponService(UserCouponRepository userCouponRepository, CouponRepository couponRepository, UserRepository userRepository, JwtService jwtService) {
        this.userCouponRepository = userCouponRepository;
        this.couponRepository = couponRepository;
        this.userRepository = userRepository;
        this.jwtService = jwtService;
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

    public UserCouponDto insertUserCoupon(UserCouponDto dto, HttpServletRequest request) {
        long idx = jwtService.extractIdx(jwtService.extractAccessToken(request).get()).get();

        // 쿠폰 코드 생성
        String code = createCouponCode();
        dto.setUcp_number(code);

        // 쿠폰 존재 여부 조회
        CouponEntity couponEntity = couponRepository.findById(dto.getCp_idx()).orElse(null);
        if(couponEntity == null) {
            throw new RuntimeException("Coupon not found for ID: " + dto.getCp_idx());
        }

        UserEntity userEntity = userRepository.findById(idx).orElse(null);
        if(userEntity == null) {
            throw new RuntimeException("User not found for ID: " + idx);
        }

        UserCouponEntity ucoupon = UserCouponEntity.toUserCouponEntity(dto, couponEntity, userEntity);
        userCouponRepository.save(ucoupon);

        return dto;
    }

    public Map<String, Object> getPageMyCoupon(int page, int size, String sortProperty, String sortDirection, HttpServletRequest request) {
        long idx = jwtService.extractIdx(jwtService.extractAccessToken(request).get()).get();
        Sort.Direction direction = Sort.Direction.fromString(sortDirection);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortProperty));
        Page<UserCouponEntity> result = userCouponRepository.findByMemberIdx(idx, pageable);
        List<UserCouponResponseDto> couponDtos = result.getContent().stream()
                .map(UserCouponResponseDto::new)
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("usercoupons", couponDtos);
        response.put("totalElements", result.getTotalElements());
        response.put("totalPages", result.getTotalPages());
        response.put("currentPage", result.getNumber() + 1);
        response.put("hasNext", result.hasNext());

        return response;
    }


}
