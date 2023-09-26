package data.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import data.dto.PointDto;
import data.dto.PointHistoryDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Calendar;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Entity(name = "point_history")
@Builder
public class PointHistoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "history_idx")
    private Integer historyIdx;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "point_idx", referencedColumnName = "point_idx")
    PointEntity pointEntity;

    @Column(name = "member_idx",nullable = false)
    private Integer memberIdx;

    @Column(name = "origin_idx", nullable = false)
    private Integer originIdx;

    @Column(nullable = false, length = 15)
    private String type;

    @Column(nullable = false)
    private Integer point;

    @CreationTimestamp
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    @Column(name = "create_date", nullable = false)
    private Date creatDate;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    @Column(name = "expire_date", nullable = false, columnDefinition = "DATE DEFAULT DATE_ADD(CURRENT_DATE, INTERVAL 60 DAY)")
    private Date expireDate;
    @PrePersist
    public void prePersist() {
        if (expireDate == null) {
            this.expireDate = Date.valueOf(LocalDate.now().plusDays(60));
        }
    }
    public static PointHistoryEntity toPointHistoryEntity(PointHistoryDto dto) {

        PointEntity pointEntity = new PointEntity();
        pointEntity.setPointIdx(dto.getPoint_idx());

        return PointHistoryEntity.builder()
                .historyIdx(dto.getHistory_idx())
                .pointEntity(pointEntity)
                .memberIdx(dto.getMember_idx())
                .originIdx(dto.getOrigin_idx())
                .point(dto.getPoint())
                .type(dto.getType())
                .creatDate(dto.getCreate_date())
                .expireDate(dto.getExpire_date())
                .build();
    }

}
