package data.service;

import data.dto.CouponDto;
import data.entity.CouponEntity;
import data.repository.CouponRepository;
import data.service.file.StorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@Service
@Slf4j
public class CouponService {
    private final CouponRepository couponRepository;
    private final StorageService storageService;
    private final String COUPON_PATH = "coupon";

    public CouponService(CouponRepository couponRepository, StorageService storageService) {
        this.couponRepository = couponRepository;
        this.storageService = storageService;
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

    // 상점에서 구매 가능한 쿠폰 리스트
    public Map<String, Object> getBuyableCoupons(int page, int size, String sortProperty, String sortDirection) {

        Sort.Direction direction = Sort.Direction.fromString(sortDirection);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortProperty));
        Page<CouponEntity> result = couponRepository.findByBuyable("Y", pageable);

        List<CouponEntity> buyable = result.getContent();

        Map<String, Object> response = new HashMap<>();
        response.put("buyable_coupons", buyable);
        response.put("totalElements", result.getTotalElements());
        response.put("totalPages", result.getTotalPages());
        response.put("currentPage", result.getNumber() + 1);
        response.put("hasNext", result.hasNext());

        return response;
    }

    public CouponEntity insertCoupon(CouponDto dto, MultipartFile upload) throws IOException {
        String fileName = storageService.saveFile(upload, COUPON_PATH);
        CouponEntity coupon = CouponEntity.toCouponEntity(dto);
        coupon.setImage(fileName);
        log.info(fileName);
        couponRepository.save(coupon);
        return coupon;
    }

    public CouponEntity updateCoupon(int cp_idx, CouponDto dto, MultipartFile upload) throws IOException, EntityNotFoundException{
        String fileName = storageService.saveFile(upload, COUPON_PATH);
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
                existingEntity.setBuyable(dto.getBuyable());
                existingEntity.setPrice(dto.getPrice());
                existingEntity.setImage(fileName);

                couponRepository.save(existingEntity);

                return existingEntity;
            } else {
                throw new EntityNotFoundException(cp_idx + "번 쿠폰 찾을 수 없음");
            }
        } catch (Exception e) {
            log.info("update coupon Error", e);
            throw e;
        }
    }

}
