package data.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import data.entity.PointEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.sql.Date;
import java.sql.Timestamp;


@Data
@AllArgsConstructor
@RequiredArgsConstructor
@ApiModel(description = "포인트 DTO")
@Builder
public class PointDto {
    @ApiModelProperty(value = "point idx", required = true)
    private int point_idx;

    @ApiModelProperty(value = "user idx", required = true)
    private long member_idx;

    @ApiModelProperty(value = "포인트 유형", required = true)
    private String type;

    @ApiModelProperty(value = "포인트", required = true)
    private int point;

    @ApiModelProperty(value = "적립 내용", required = true)
    private String comment;

    @ApiModelProperty(value = "발급일", required = true)
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private Date create_date;

    @ApiModelProperty(value = "만료일", required = true)
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private Date expire_date;

    @ApiModelProperty(value = "총 보유 포인트")
    private int sumPoint;
    public static PointDto toPointDto(PointEntity entity) {

        return PointDto.builder()
                .point_idx(entity.getPointIdx())
                .member_idx(entity.getUserEntity().getIdx())
                .type(entity.getType())
                .point(entity.getPoint())
                .comment(entity.getComment())
                .create_date(entity.getCreateDate())
                .expire_date(entity.getExpireDate())
                .build();
    }

}
