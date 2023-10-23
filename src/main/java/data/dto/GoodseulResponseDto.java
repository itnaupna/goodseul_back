package data.dto;


import data.entity.FavoriteEntity;
import data.entity.GoodseulEntity;
import data.entity.UserEntity;
import data.repository.FavoriteRepository;
import data.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@AllArgsConstructor
@RequiredArgsConstructor
@Data
@Builder
public class GoodseulResponseDto {

    private Long gIdx;
    private String goodseulName;
    private String userProfile;
    private Integer isPremium;
    private int favoriteCount;

    public GoodseulResponseDto(UserEntity user) {

        GoodseulEntity goodseul = user.getIsGoodseul();
        this.gIdx = goodseul.getIdx();
        this.goodseulName = goodseul.getGoodseulName();
        this.isPremium = goodseul.getIsPremium();

        this.userProfile = user.getUserProfile();
        this.favoriteCount = this.getFavoriteCount();

    }

}
