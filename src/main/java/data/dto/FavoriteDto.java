package data.dto;

import data.entity.FavoriteEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@AllArgsConstructor
@RequiredArgsConstructor
@Data
@Builder
public class FavoriteDto {
    private int f_idx;
    private Long u_idx;
    private Long g_idx;
    public static FavoriteDto toFavoriteDto(FavoriteEntity entity) {
        return FavoriteDto.builder()
                .f_idx(entity.getFIdx())
                .u_idx(entity.getUserEntity().getIdx())
                .g_idx(entity.getGoodseulEntity().getIdx())
                .build();
    }

}
