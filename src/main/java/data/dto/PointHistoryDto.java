package data.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import data.entity.PointHistoryEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.sql.Date;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
public class PointHistoryDto {
    private int history_idx;
    private int point_idx;
    private int origin_idx;
    private int member_idx;
    private String type;
    private int point;
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private Date create_date; // 발급일
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private Date expire_date; // 만료일

    public static PointHistoryDto toPointHistoryDto(PointHistoryEntity entity) {
        return PointHistoryDto.builder()
                .history_idx(entity.getHistoryIdx())
                .origin_idx(entity.getOriginIdx())
                .member_idx(entity.getMemberIdx())
                .type(entity.getType())
                .point(entity.getPoint())
                .create_date(entity.getCreatDate())
                .expire_date(entity.getExpireDate())
                .build();
    }

}
