package data.dto;

import data.entity.GoodseulEntity;
import data.entity.UserEntity;
import jwt.setting.config.SocialType;
import lombok.*;

@AllArgsConstructor
@RequiredArgsConstructor
@Data
@Builder

public class UserDto {
    private Long idx;
    private String email;
    private String name;
    private String password;
    private String nickname;
    private String phoneNumber;
    private String location;
    private String birth;
    private String userProfile;
    private Long isGoodseul;
    private String role;
    private String socialId;
    private SocialType socialType;

    public static UserDto toUserDto (UserEntity entity) {

        return UserDto.builder()
                .idx(entity.getIdx())
                .email(entity.getEmail())
                .name(entity.getName())
                .password(entity.getPassword())
                .nickname(entity.getNickname())
                .phoneNumber(entity.getPhoneNumber())
                .location(entity.getLocation())
                .birth(entity.getBirth())
                .userProfile(entity.getUserProfile())
                .isGoodseul(entity.getIsGoodseul().getIdx())
                .build();
    }

}

