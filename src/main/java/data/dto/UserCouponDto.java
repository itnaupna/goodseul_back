package data.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
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
    private String ucp_number;
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private Timestamp u_create_date;
    private long member_idx;
    private String ucp_status;
    private int cp_idx;

    public static UserCouponDto toUserCouponDto(UserCouponEntity entity) {

        return UserCouponDto.builder()
                .ucp_idx(entity.getUcpIdx())
                .ucp_number(entity.getCpNumber())
                .u_create_date(entity.getUcpCreateDate())
                .member_idx(entity.getUserEntity().getIdx())
                .ucp_status(entity.getUcpStatus())
                .cp_idx(entity.getCoupon().getCpIdx())
                .build();
    }

}
