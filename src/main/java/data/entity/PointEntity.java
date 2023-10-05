package data.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import data.dto.PointDto;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Optional;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
@Entity(name = "point")
public class PointEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "point_idx")
    private Integer pointIdx;

// user idx join 필요
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "member_idx", referencedColumnName = "idx", nullable = false)
//    private UserEntity userEntity;

    @Column(name = "member_idx", nullable = false)
    private Integer memberIdx;

    @Column(length = 1, nullable = false)
    private String type;

    @Column(nullable = false)
    private Integer point;

    @Column(length = 50, nullable = false)
    private String comment;

    @CreationTimestamp
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    @Column(name = "create_date", nullable = false, columnDefinition = "DATE DEFAULT CURRENT_DATE")
    private Date createDate;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    @Column(name = "expire_date",  columnDefinition = "DATE DEFAULT DATE_ADD(CURRENT_DATE, INTERVAL 60 DAY)")
    private Date expireDate;
    @PrePersist
    public void prePersist() {
        if (expireDate == null) {
            this.expireDate = Date.valueOf(LocalDate.now().plusDays(60));
        }
    }
    public static PointEntity toPointEntity (PointDto dto) {
        return PointEntity.builder()
                .pointIdx(dto.getPoint_idx())
                .memberIdx(dto.getMember_idx())
                .type(dto.getType())
                .point(dto.getPoint())
                .comment(dto.getComment())
                .createDate(dto.getCreate_date())
                .expireDate(dto.getExpire_date())
                .build();
    }

}
