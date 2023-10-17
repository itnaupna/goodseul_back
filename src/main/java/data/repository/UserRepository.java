package data.repository;

import data.dto.GoodseulDto;
import data.entity.GoodseulEntity;
import jwt.setting.config.SocialType;
import data.entity.UserEntity;
import org.apache.catalina.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByEmail(String email);
    Optional<UserEntity> findByNickname(String nickname);
    Optional<UserEntity> findByIdx(Long idx);
    Optional<UserEntity> findByRefreshToken(String refreshToken);
    Optional<UserEntity> findByPhoneNumber(String phoneNumber);
    Optional<UserEntity> findBySocialTypeAndSocialId(SocialType socialType, String socialId);

    Optional<UserEntity> findByIsGoodseul(Long idGoodseul);
    Page<UserEntity> findAll(Pageable pageable);


    @Query("SELECT new data.dto.GoodseulDto(g.idx, g.goodseulName, g.skill, g.career, g.goodseulProfile, g.goodseulInfo) FROM GoodseulEntity g WHERE g.idx IN (SELECT u.isGoodseul.idx FROM UserEntity u WHERE u.location = :location)")
    Page<GoodseulDto> findGoodseulIdxByLocation(@Param("location") String location, Pageable pageable);

    @Transactional
    @Modifying
    @Query("UPDATE UserEntity u SET u.refreshToken = NULL WHERE u.idx = ?1")
    void removeRefreshTokenByIdx(Long idx);

    void deleteAllByIdx(Long idx);

    boolean existsByEmail(String email);
    boolean existsByNickname(String nickname);
    boolean existsByPhoneNumber(String phoneNumber);


}
