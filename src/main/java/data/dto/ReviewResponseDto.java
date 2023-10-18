package data.dto;

import data.entity.GoodseulEntity;
import data.entity.ReviewEntity;
import data.entity.ReviewLikeEntity;
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

    // 구슬 정보
    private Long gIdx;
    private String goodseulName;
    private String skill;
    private Integer isPremium;
    private String goodseulProfile;

    // 유저 정보
    private Long uIdx;
    private String uNick;
    private String uProfile;

    // 좋아요
    private Integer likeCount;

    // 구슬의 리뷰 제목
    private String randSubject;

    public ReviewResponseDto(ReviewEntity review, Integer likeCount) {
        this.rIdx = review.getRIdx();
        this.rSubject = review.getRSubject();
        this.rContent = review.getRContent();
        this.star = review.getStar();
        this.rType = review.getRType();
        this.rCreateDate = review.getRCreateDate();

        GoodseulEntity goodseul = review.getGoodseulEntity();
        this.gIdx = goodseul.getIdx();
        this.goodseulName = goodseul.getGoodseulName();
        this.skill = goodseul.getSkill();
        this.isPremium = goodseul.getIsPremium();

        UserEntity user = review.getUserEntity();
        this.uIdx = user.getIdx();
        this.uNick = user.getNickname();
        this.uProfile = user.getUserProfile();

        this.likeCount = likeCount;
        this.randSubject = randSubject;

    }

}
