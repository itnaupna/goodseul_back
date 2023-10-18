package data.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import data.entity.CouponEntity;
import lombok.*;


import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
public class CouponDto {
    private int cp_idx;
    private String cp_name;
    private String cp_description;
    private String cp_type;
    private int discount_amount;
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private Date start_date;
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private Date end_date;
    private String cp_status;
    private int price;
    private String image;
    private String buyable;

    public static CouponDto toCouponDto(CouponEntity entity) {

        return CouponDto.builder()
                .cp_idx(entity.getCpIdx())
                .cp_name(entity.getCpName())
                .cp_description(entity.getCpDescription())
                .cp_type(entity.getCpType())
                .discount_amount(entity.getDiscountAmount())
                .start_date(entity.getStartDate())
                .end_date(entity.getEndDate())
                .cp_status(entity.getCpStatus())
                .price(entity.getPrice())
                .image(entity.getImage())
                .buyable(entity.getBuyable())
                .build();
    }

}

