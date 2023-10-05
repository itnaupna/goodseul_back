package data.entity;

import data.dto.ReviewLikeDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Entity(name = "review_like")
@Builder
public class ReviewLikeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer rlIdx;

    @OneToOne
    @JoinColumn(name = "r_idx", referencedColumnName = "r_idx", nullable = false)
    private ReviewEntity reviewEntity;

    @ManyToOne
    @JoinColumn(name = "u_idx", referencedColumnName = "idx", nullable = false)
    private UserEntity userEntity;

    public static ReviewLikeEntity toReviewLikeEntity (ReviewLikeDto dto, ReviewEntity review, UserEntity user) {
        return ReviewLikeEntity.builder()
                .rlIdx(dto.getRl_idx())
                .reviewEntity(review)
                .userEntity(user)
                .build();
    }

}
