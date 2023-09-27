package data.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RankResponseDto {

    private String userIdx;
    private int rank;
    private double score;
    private long date;

}
