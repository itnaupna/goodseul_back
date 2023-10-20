package data.dto;

import data.entity.ReviewLikeEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@ApiModel(description = "리뷰 좋아요 DTO")
@RequiredArgsConstructor
@Builder
public class ReviewLikeDto {

    @ApiModelProperty(value = "리뷰 좋아요 idx", required = true)
    private int rl_idx;

    @ApiModelProperty(value = "리뷰 idx", required = true)
    private int r_idx;

    @ApiModelProperty(value = "유저 idx", required = true)
    private Long u_idx;

    @ApiModelProperty(value = "좋아요")
    private int like;
    public static ReviewLikeDto toReviewLikeDto (ReviewLikeEntity entity) {
        return ReviewLikeDto.builder()
                .rl_idx(entity.getRlIdx())
                .r_idx(entity.getReviewEntity().getRIdx())
                .u_idx(entity.getUserEntity().getIdx())
                .build();
    }

}
