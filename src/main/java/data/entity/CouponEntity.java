package data.entity;

import data.dto.CouponDto;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Entity(name = "coupon")
@Builder
public class CouponEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cp_idx")
    private Integer cpIdx;

    @Column(name = "cp_name", nullable = false, length = 20)
    private String cpName;

    @Column(name = "cp_description", length = 50)
    private String cpDescription;

    @Column(name = "cp_type", nullable = false)
    private String cpType;

    @Column(name = "discount_amount", nullable = false)
    private Integer discountAmount;

    @Column(name = "start_date", nullable = false)
    private Date startDate;

    @Column(name = "end_date", nullable = false)
    private Date endDate;

    @Column(name = "cp_status", columnDefinition = "VARCHAR(1) DEFAULT 'N'", nullable = false, length = 1)
    private String cpStatus;

    @CreationTimestamp
    @Column(name = "create_date", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP", nullable = false)
    private Timestamp createDate;

    @OneToMany(mappedBy = "coupon", cascade = CascadeType.ALL)
    private List<UserCouponEntity> userCoupons;

    @PrePersist
    public void prePersist() {
        if (cpStatus == null) {
            this.cpStatus = "N";
        }
    }

    public static CouponEntity toCouponEntity (CouponDto dto) {
        return CouponEntity.builder()
                .cpIdx(dto.getCp_idx())
                .cpName(dto.getCp_name())
                .cpDescription(dto.getCp_description())
                .cpType(dto.getCp_type())
                .discountAmount(dto.getDiscount_amount())
                .startDate(dto.getStart_date())
                .endDate(dto.getEnd_date())
                .cpStatus(dto.getCp_status())
                .createDate(dto.getCreate_date())
                .build();
    }

}
