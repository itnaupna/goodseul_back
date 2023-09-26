package data.entity;
import jwt.setting.config.Role;
import jwt.setting.config.SocialType;
import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Builder
@Table(name="goodseulInfo")
@AllArgsConstructor
public class GoodseulEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;
    private String email;
    private String password;
    private String location;
    private String phoneNumber;
    private String birth;
    private String goodseulName;
    private String skill;
    private String career;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Enumerated(EnumType.STRING)
    private SocialType socialType;
    private String socialId;
    private String refreshToken;

    public void authorizeUser() {
        this.role = Role.GOODSEUL;
    }
    public void passwordEncode(PasswordEncoder passwordEncoder){
        this.password = passwordEncoder.encode(this.password);
    }
    public void updateRefreshToken(String updateRefreshToken){
        this.refreshToken = updateRefreshToken;
    }

}