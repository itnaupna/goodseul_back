package data.dto;

import data.entity.GoodseulEntity;
import data.entity.ReviewEntity;
import data.entity.UserEntity;
import lombok.Data;

import java.sql.Timestamp;

@Data
public class ReviewResponseDto {
    private Integer rIdx;
    private String rSubject;
    private String rContent;
    private Integer star;
    private String rType;
    private Timestamp rCreateDate;

    // 구슬 정보 ( 사진도 필요함 )
    private Long gIdx;
    private String goodseulName;
    private String skill;
    private Integer isPremium;

    // 유저 정보
    private  Long uidx;
    private  String uName;


    public ReviewResponseDto(ReviewEntity review) {
        this.rIdx = review.getRIdx();
        this.rSubject = review.getRSubject();
        this.rContent = review.getRContent();
        this.star = review.getStar();
        this.rType = review.getRType();
        this.rCreateDate = review.getRCreateDate();

        GoodseulEntity goodseul = review.getGIdx();
        this.gIdx = goodseul.getIdx();
        this.goodseulName = goodseul.getGoodseulName();
        this.skill = goodseul.getSkill();
        this.isPremium = goodseul.getIs_premium();

        UserEntity user = review.getUIdx();
        this.uidx = user.getIdx();
        this.uName = user.getName();

    }
}
