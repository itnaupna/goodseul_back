package data.dto;

import data.entity.OfferEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OfferDto {

    private int offerIdx;
    private long userIdx;
    private String purpose;
    private Timestamp desiredDate;
    private String location;
    private String details;
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
