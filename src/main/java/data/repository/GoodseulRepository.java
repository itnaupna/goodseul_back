package data.repository;




import data.entity.GoodseulEntity;
import data.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface GoodseulRepository extends JpaRepository<GoodseulEntity, Long> {
    Optional<GoodseulEntity> findByIdx(Long idx);
    Page<GoodseulEntity> findAll(Pageable pageable);

    @Modifying
    @Query("UPDATE GoodseulEntity g SET g.career = :career, g.skill = :skill, g.goodseulName = :goodseulName WHERE g.idx = :idx")
    void updateAllBy(@Param("idx") Long idx, @Param("career") String career, @Param("skill") String skill, @Param("goodseulName") String goodseulName);
}
