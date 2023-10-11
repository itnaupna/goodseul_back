package data.dto;

import data.entity.GoodseulEntity;
import lombok.*;

import java.sql.Timestamp;

@AllArgsConstructor
@RequiredArgsConstructor
@Data
@Builder
public class GoodseulDto {
    private Long idx;
    private String goodseulName;
    private String skill;
    private String career;
    private int isPremium;
    private Timestamp premiumDate;
    private String goodseulProfile;

    public GoodseulDto(Long idx, String goodseulName, String skill, String career) {
        this.idx = idx;
        this.goodseulName = goodseulName;
        this.skill = skill;
        this.career =career;
    }
  
    public static GoodseulDto toGoodseulDto (GoodseulEntity entity){
        return GoodseulDto.builder()
                .idx(entity.getIdx())
                .goodseulName(entity.getGoodseulName())
                .skill(entity.getSkill())
                .career(entity.getCareer())
                .isPremium(entity.getIsPremium())
                .premiumDate(entity.getPremiumDate())
                .goodseulProfile(entity.getGoodseulProfile())
                .build();
    }
}
