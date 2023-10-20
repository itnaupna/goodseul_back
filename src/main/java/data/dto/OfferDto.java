package data.dto;

import data.entity.OfferEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "견적 DTO")
public class OfferDto {

    @ApiModelProperty(value = "견적 인덱스", example = "1")
    private int offerIdx;

    @ApiModelProperty(value = "사용자 인덱스", example = "10001")
    private long userIdx;

    @ApiModelProperty(value = "견적 목적", example = "건강")
    private String purpose;

    @ApiModelProperty(value = "원하는 날짜", example = "2023-10-19T10:15:30.00Z")
    private Timestamp desiredDate;

    @ApiModelProperty(value = "위치", example = "서울")
    private String location;

    @ApiModelProperty(value = "견적 상세 내용", example = "건강을 회복하고싶어서 굿을 하고 싶습니다.")
    private String details;

    @ApiModelProperty(value = "작성 날짜", example = "2023-10-18T10:15:30.00Z")
    private Timestamp writeDate;

    public static OfferDto offerEntityToDto(OfferEntity entity) {
        return OfferDto.builder()
                .offerIdx(entity.getOfferIdx())
                .userIdx(entity.getUser().getIdx())
                .purpose(entity.getPurpose())
                .desiredDate(entity.getDesiredDate())
                .location(entity.getLocation())
                .details(entity.getDetails())
                .writeDate(entity.getWriteDate())
                .build();
    }
}