package data.repository;

import jwt.setting.config.SocialType;
import data.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByEmail(String email);
    Optional<UserEntity> findByNickname(String nickname);
    Optional<UserEntity> findByIdx(long idx);
    Optional<UserEntity> findByRefreshToken(String refreshToken);
    Optional<UserEntity> findByPhoneNumber(String phoneNumber);
    Optional<UserEntity> findBySocialTypeAndSocialId(SocialType socialType, String socialId);
}
