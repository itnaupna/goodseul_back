package data.service;

import data.dto.CouponDto;
import data.entity.CouponEntity;
import data.repository.CouponRepository;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@Service
@Slf4j
public class CouponService {

    @Autowired
    private final CouponRepository couponRepository;

    public CouponService(CouponRepository couponRepository) {
        this.couponRepository = couponRepository;
    }

    public Map<String, Object> getPageCoupons(int page, int size, String sortProperty, String sortDirection, String keyword) {

        Sort.Direction direction = Sort.Direction.fromString(sortDirection);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortProperty));
        Page<CouponEntity> result;

        if (keyword != null && !keyword.trim().isEmpty()) {
            result = couponRepository.findByCpNameContainingOrCpDescriptionContaining(keyword, keyword, pageable);
            log.info("Keyword: " + keyword);
        } else {
            result = couponRepository.findAll(pageable);
        }

        List<CouponEntity> coupons = result.getContent();

        Map<String, Object> response = new HashMap<>();
        response.put("coupons", coupons);
        response.put("totalElements", result.getTotalElements());
        response.put("totalPages", result.getTotalPages());
        response.put("currentPage", result.getNumber() + 1);
        response.put("hasNext", result.hasNext());

        return response;
    }

    public CouponDto insertCoupon(CouponDto dto) {
        CouponEntity coupon = CouponEntity.toCouponEntity(dto);
        couponRepository.save(coupon);
        return dto;
    }

    public void updateCoupon(int cp_idx, CouponDto dto){
        try {
            Optional<CouponEntity> e = couponRepository.findById(cp_idx);

            if(e.isPresent()) {
                CouponEntity existingEntity = e.get();
                existingEntity.setCpName(dto.getCp_name());
                existingEntity.setCpDescription(dto.getCp_description());
                existingEntity.setCpType(dto.getCp_type());
                existingEntity.setStartDate(dto.getStart_date());
                existingEntity.setEndDate(dto.getEnd_date());
                existingEntity.setCpStatus(dto.getCp_status());
                // Update the existing entity
                couponRepository.save(existingEntity);
            }
        } catch (Exception e) {
            log.info("update coupon Error", e);
            throw e;
        }
    }

}
