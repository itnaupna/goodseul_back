package data.entity;

import data.dto.OfferDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.sql.Timestamp;

@Data
@Entity(name = "Offer")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OfferEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int offerIdx;

    @ManyToOne
    @JoinColumn(name = "userIdx", referencedColumnName = "idx")
    UserEntity user;

    private String purpose;
    private Timestamp desiredDate;
    private String location;
    private String details;
    private Timestamp writeDate;

    public static OfferEntity offerDtoToEntity(OfferDto dto,UserEntity user) {
        return OfferEntity.builder()
                .offerIdx(dto.getOfferIdx())
                .user(user)
                .purpose(dto.getPurpose())
                .desiredDate(dto.getDesiredDate())
                .location(dto.getLocation())
                .details(dto.getDetails())
                .writeDate(dto.getWriteDate())
                .build();
    }

}
