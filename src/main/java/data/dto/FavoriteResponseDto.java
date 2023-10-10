package data.dto;

import data.entity.FavoriteEntity;
import data.entity.GoodseulEntity;

import data.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class FavoriteResponseDto {
    private int f_idx;
    private Long u_idx;
    private Long g_idx;
    private int favoriteCount;

    public FavoriteResponseDto(FavoriteEntity favorite, Integer favoriteCount){
        this.f_idx = favorite.getFIdx();
        this.favoriteCount = favoriteCount;

        GoodseulEntity goodseul = favorite.getGoodseulEntity();
        this.g_idx = goodseul.getIdx();

        UserEntity user = favorite.getUserEntity();
        this.u_idx = user.getIdx();

    }
}
