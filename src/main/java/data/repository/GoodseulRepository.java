package data.repository;




import data.dto.GoodseulDto;
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
    @Query("SELECT new data.dto.GoodseulDto(g.idx, g.goodseulName, g.skill, g.career, g.goodseulProfile, g.goodseulInfo) FROM GoodseulEntity g WHERE g.skill = :skill")
    Page<GoodseulDto> findGoodseulIdxBySkill(@Param("skill") String skill, Pageable pageable);
}
