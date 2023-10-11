package data.entity;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import data.dto.ReviewDto;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.sql.Timestamp;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Entity(name = "review")
@Builder
public class ReviewEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "r_idx")

    private Integer rIdx;

    @Column(nullable = false)
    private String rSubject;

    @Column(nullable = false)
    private String rContent;

    @Column(nullable = false)
    private Integer star;

    @Column(nullable = false)
    private String rType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "g_idx", referencedColumnName = "idx", nullable = false)
    private GoodseulEntity goodseulEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "u_idx", referencedColumnName = "idx", nullable = false)
    private UserEntity userEntity;

    @CreationTimestamp
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    @Column(nullable = false)
    private Timestamp rCreateDate;

    public static ReviewEntity toReviewEntity(ReviewDto dto, GoodseulEntity goodseul, UserEntity user) {
        return ReviewEntity.builder()
                .rIdx(dto.getR_idx())
                .rSubject(dto.getR_subject())
                .rContent(dto.getR_content())
                .star(dto.getStar())
                .rType(dto.getR_type())
                .goodseulEntity(goodseul)
                .userEntity(user)
                .rCreateDate(dto.getR_create_date())
                .build();
    }

}
