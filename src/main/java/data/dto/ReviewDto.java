package data.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import data.entity.ReviewEntity;
import lombok.*;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
public class ReviewDto {

    private int r_idx;
    private String r_subject;
    private String r_content;
    private int star;
    private String r_type;
    private Long g_idx;
    private Long u_idx;
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
