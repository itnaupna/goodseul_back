package data.entity;

import data.dto.FavoriteDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Entity(name = "favorite")
@Builder
public class FavoriteEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer fIdx;

    @ManyToOne
    @JoinColumn(name = "u_idx", referencedColumnName = "idx", nullable = false)
    UserEntity userEntity;

    @ManyToOne
    @JoinColumn(name = "g_idx", referencedColumnName = "idx", nullable = false)
    GoodseulEntity goodseulEntity;

    public static FavoriteEntity toFavoriteEntity (FavoriteDto dto, UserEntity user, GoodseulEntity goodseul) {
        return FavoriteEntity.builder()
                .fIdx(dto.getF_idx())
                .userEntity(user)
                .goodseulEntity(goodseul)
                .build();
    }

}
