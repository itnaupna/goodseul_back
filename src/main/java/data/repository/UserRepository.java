package data.repository;

import data.dto.GoodseulDto;
import jwt.setting.config.SocialType;
import data.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByEmail(String email);
    Optional<UserEntity> findByNickname(String nickname);
    Optional<UserEntity> findByIdx(long idx);
    Optional<UserEntity> findByRefreshToken(String refreshToken);
    Optional<UserEntity> findByPhoneNumber(String phoneNumber);
    Optional<UserEntity> findBySocialTypeAndSocialId(SocialType socialType, String socialId);

    Page<UserEntity> findAll(Pageable pageable);

    @Query("SELECT new data.dto.GoodseulDto(g.idx, g.goodseulName, g.skill, g.career) FROM GoodseulEntity g WHERE g.idx IN (SELECT u.isGoodseul.idx FROM UserEntity u WHERE u.location = :location)")
    List<GoodseulDto> findGoodseulIdxByLocation(@Param("location") String location);

    @Transactional
    @Modifying
    @Query("UPDATE UserEntity u SET u.refreshToken = NULL WHERE u.idx = ?1")
    void removeRefreshTokenByIdx(Long idx);

    void deleteAllByIdx(Long idx);

    @Modifying
    @Query("UPDATE UserEntity u SET u.name = :name, u.nickname = :nickname, u.phoneNumber = :phoneNumber WHERE u.idx = :idx")
    void updateAllBy(@Param("idx") Long idx, @Param("name") String name, @Param("nickname") String email, @Param("phoneNumber") String phoneNumber);


}
