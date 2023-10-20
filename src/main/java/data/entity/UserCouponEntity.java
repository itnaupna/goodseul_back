package data.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import data.dto.UserCouponDto;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.security.core.userdetails.User;

import javax.persistence.*;
import java.sql.Timestamp;


@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Entity(name = "user_coupon")
@Builder
public class UserCouponEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ucp_idx")
    private Integer ucpIdx;

    @Column(name = "ucp_number", nullable = false, length = 20)
    private String cpNumber;

    @CreationTimestamp
    @Column(name = "ucp_create_date", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private Timestamp ucpCreateDate ;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_idx", referencedColumnName = "idx", nullable = false)
    private UserEntity userEntity;

    @Column(name = "ucp_status", columnDefinition = "VARCHAR(1) DEFAULT 'N'", nullable = false, length = 1)
    private String ucpStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cp_idx", referencedColumnName = "cp_idx", updatable = false, insertable = true)
    private CouponEntity coupon;
    @PrePersist
    public void prePersist() {
        if (ucpStatus == null) {
            this.ucpStatus = "N";
        }
    }

    public static  UserCouponEntity toUserCouponEntity(UserCouponDto dto, CouponEntity couponEntity, UserEntity userEntity) {
        return UserCouponEntity.builder()
                .ucpIdx(dto.getUcp_idx())
                .cpNumber(dto.getUcp_number())
                .ucpCreateDate(dto.getU_create_date())
                .userEntity(userEntity)
                .ucpStatus(dto.getUcp_status())
                .coupon(couponEntity)
                .build();
    }

}
