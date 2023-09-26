package data.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import data.dto.UserCouponDto;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;


@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Entity(name = "user_coupon")
@Builder
public class UserCouponEntity {

    @Id
    @Column(name = "ucp_idx")
    private Integer ucpIdx;

    @Column(name = "cp_number", nullable = false, length = 20)
    private String cpNumber;

    @Column(name = "create_date", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private Timestamp createDate;

    @Column(name = "member_idx", nullable = false)
    private Integer memberIdx;

    @Column(name = "cp_status", columnDefinition = "VARCHAR(1) DEFAULT 'N'", nullable = false, length = 1)
    private String cpStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cp_idx", referencedColumnName = "cp_idx", updatable = false, insertable = true)
    private CouponEntity coupon;


    public static  UserCouponEntity toUserCouponEntity(UserCouponDto dto, CouponEntity couponEntity) {
        return UserCouponEntity.builder()
                .ucpIdx(dto.getUcp_idx())
                .cpNumber(dto.getCp_number())
                .createDate(dto.getCreate_date())
                .memberIdx(dto.getMember_idx())
                .cpStatus(dto.getCp_status())
                .coupon(couponEntity)
                .build();
    }

}
