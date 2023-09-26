package data.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import data.entity.CouponEntity;
import data.entity.UserCouponEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
public class UserCouponDto {
    private int ucp_idx;
    private String cp_number;
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private Timestamp create_date;
    private int member_idx;
    private String cp_status;
    private int cp_idx;

    public static UserCouponDto toUserCouponDto(UserCouponEntity entity) {

        return UserCouponDto.builder()
                .ucp_idx(entity.getUcpIdx())
                .cp_number(entity.getCpNumber())
                .create_date(entity.getCreateDate())
                .member_idx(entity.getMemberIdx())
                .cp_status(entity.getCpStatus())
                .cp_idx(entity.getCoupon().getCpIdx())
                .build();
    }

}
