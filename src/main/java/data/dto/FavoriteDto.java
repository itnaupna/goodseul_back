package data.dto;

import data.entity.FavoriteEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@AllArgsConstructor
@RequiredArgsConstructor
@Data
@Builder
@ApiModel(description = "찜목록 DTO")
public class FavoriteDto {
    @ApiModelProperty(value = "favorite idx", required = false)
    private int f_idx;
    @ApiModelProperty(value = "구슬 idx", required = true)
    private Long u_idx;
    @ApiModelProperty(value = "유저 idx", required = false)
    private Long g_idx;
    private boolean favoriteStatus;
    public static FavoriteDto toFavoriteDto(FavoriteEntity entity) {
        return FavoriteDto.builder()
                .f_idx(entity.getFIdx())
                .u_idx(entity.getUserEntity().getIdx())
                .g_idx(entity.getGoodseulEntity().getIdx())
                .build();
    }

}
