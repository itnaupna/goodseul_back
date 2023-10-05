package data.dto;

import data.entity.ReviewLikeEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
public class ReviewLikeDto {

    private int rl_idx;
    private int r_idx;
    private Long u_idx;
    private int like;
    public static ReviewLikeDto toReviewLikeDto (ReviewLikeEntity entity) {
        return ReviewLikeDto.builder()
                .rl_idx(entity.getRlIdx())
                .r_idx(entity.getReviewEntity().getRIdx())
                .u_idx(entity.getUserEntity().getIdx())
                .build();
    }

}
