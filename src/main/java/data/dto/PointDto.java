package data.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import data.entity.PointEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.sql.Date;
import java.sql.Timestamp;


@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
public class PointDto {
    private int point_idx; // 포인트 아이디
    private int member_idx; // 멤버 아이디
    private String type;
    private int point; // 포인트
    private String comment;
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private Date create_date; // 발급일
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private Date expire_date; // 만료일
    private int sumPoint;

    public static PointDto toPointDto(PointEntity entity) {

        return PointDto.builder()
                .point_idx(entity.getPointIdx())
                .member_idx(entity.getMemberIdx())
                .type(entity.getType())
                .point(entity.getPoint())
                .comment(entity.getComment())
                .create_date(entity.getCreateDate())
                .expire_date(entity.getExpireDate())
                .build();
    }

}
