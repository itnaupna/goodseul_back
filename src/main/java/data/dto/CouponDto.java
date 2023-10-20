package data.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import data.entity.CouponEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;


import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
@ApiModel(description = "쿠폰 상세 정보")
public class CouponDto {
    @ApiModelProperty(value = "쿠폰 아이디")
    private int cp_idx;

    @ApiModelProperty(value = "쿠폰 이름", required = true)
    private String cp_name;

    @ApiModelProperty(value = "쿠폰 설명", required = false)
    private String cp_description;

    @ApiModelProperty(value = "쿠폰 유형" , required = true)
    private String cp_type;

    @ApiModelProperty(value = "할인 금액, 할인 퍼센트, 교환권 등", required = true)
    private int discount_amount;

    @ApiModelProperty(value = "시작 날짜", example = "2023-10-19", required = false)
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private Date start_date;

    @ApiModelProperty(value = "종료 날짜", example = "2023-12-31", required = false)
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private Date end_date;

    @ApiModelProperty(value = "쿠폰 사용 가능 여부", required = true)
    private String cp_status;

    @ApiModelProperty(value = "가격", required = true)
    private int price;

    @ApiModelProperty(value = "이미지 URL", required = false)
    private String image;

    @ApiModelProperty(value = "상점 구매 가능 여부", required = true)
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

