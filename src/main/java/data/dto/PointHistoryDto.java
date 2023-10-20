package data.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import data.entity.PointHistoryEntity;
import data.entity.UserEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.sql.Date;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@ApiModel(description = "포인트 히스토리 DTO")
@Builder
public class PointHistoryDto {
    @ApiModelProperty(value = "히스토리 idx")
    private int history_idx;

    @ApiModelProperty(value = "포인트 idx")
    private int point_idx;

    @ApiModelProperty(value = "포인트 테이블의 원본 인덱스")
    private int origin_idx;

    @ApiModelProperty(value = "user idx")
    private long member_idx;

    @ApiModelProperty(value = "포인트 유형")
    private String type;

    @ApiModelProperty(value = "포인트")
    private int point;

    @ApiModelProperty(value = "발급일")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private Date create_date;

    @ApiModelProperty(value = "만료일")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private Date expire_date;


    public static PointHistoryDto toPointHistoryDto(PointHistoryEntity entity) {

        return PointHistoryDto.builder()
                .history_idx(entity.getHistoryIdx())
                .origin_idx(entity.getOriginIdx())
                .member_idx(entity.getUserEntity().getIdx())
                .type(entity.getType())
                .point(entity.getPoint())
                .create_date(entity.getCreatDate())
                .expire_date(entity.getExpireDate())
                .build();
    }

}
