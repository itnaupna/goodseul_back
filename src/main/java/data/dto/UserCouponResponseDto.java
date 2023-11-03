package data.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import data.entity.CouponEntity;
import data.entity.UserCouponEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.sql.Date;
import java.sql.Timestamp;

@AllArgsConstructor
@RequiredArgsConstructor
@Data
@Builder
public class UserCouponResponseDto {

    private Integer ucp_idx;
    private String cp_name;
    private String ucp_status;
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private Date start_date;
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private Date end_date;
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private Timestamp ucp_create_date;
    private Integer discount_amount;
    private String cp_type;
    private Long member_idx;
    private String ucp_number;
    private String cp_description;
    private Integer price;
    private String image;
    private String buyable;


    public UserCouponResponseDto(UserCouponEntity userCoupon) {

        this.ucp_idx = userCoupon.getUcpIdx();
        this.ucp_status = userCoupon.getUcpStatus();
        this.ucp_create_date = userCoupon.getUcpCreateDate();
        this.member_idx = userCoupon.getUserEntity().getIdx();
        this.ucp_number = userCoupon.getCpNumber();

        CouponEntity coupon = userCoupon.getCoupon();
        this.cp_name = coupon.getCpName();
        this.cp_type = coupon.getCpType();
        this.start_date = coupon.getStartDate();
        this.end_date = coupon.getEndDate();
        this.cp_description = coupon.getCpDescription();
        this.price = coupon.getPrice();
        this.image = coupon.getImage();

    }



}
