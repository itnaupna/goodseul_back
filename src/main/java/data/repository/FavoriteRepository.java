package data.repository;

import data.entity.FavoriteEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteRepository extends JpaRepository <FavoriteEntity, Integer> {
    boolean existsByUserEntity_idxAndGoodseulEntity_idx(Long u_idx, Long g_idx);
    Optional<FavoriteEntity> findByUserEntity_idxAndGoodseulEntity_idx(Long u_idx, Long g_idx);
    Integer countFavoriteEntitiesByGoodseulEntity_idx(Long g_idx);
    Page<FavoriteEntity> findByUserEntity_idx(Long u_idx, Pageable pageable);
    int countAllByUserEntity_Idx(long u_idx);
}
