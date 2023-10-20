package data.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import data.entity.ReviewEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@ApiModel(description = "리뷰 상세 정보")
@Builder
public class ReviewDto {

    @ApiModelProperty(value = "리뷰 인덱스", required = true)
    private int r_idx;

    @ApiModelProperty(value = "리뷰 제목", required = true)
    private String r_subject;

    @ApiModelProperty(value = "리뷰 내용", required = true)
    private String r_content;

    @ApiModelProperty(value = "별점", required = true)
    private int star;

    @ApiModelProperty(value = "리뷰 타입", required = true)
    private String r_type;

    @ApiModelProperty(value = "구슬 idx", required = true)
    private Long g_idx;

    @ApiModelProperty(value = "유저 idx", required = true)
    private Long u_idx;

    @ApiModelProperty(value = "리뷰 작성 날짜", required = true)
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private Timestamp r_create_date;

    public static ReviewDto toReviewDto (ReviewEntity entity) {

        return ReviewDto.builder()
                .r_idx(entity.getRIdx())
                .r_subject(entity.getRSubject())
                .r_content(entity.getRContent())
                .star(entity.getStar())
                .r_type(entity.getRType())
                .g_idx(entity.getGoodseulEntity().getIdx())
                .u_idx(entity.getUserEntity().getIdx())
                .r_create_date(entity.getRCreateDate())
                .build();
    }

}
