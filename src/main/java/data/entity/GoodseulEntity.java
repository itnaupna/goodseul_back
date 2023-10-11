package data.entity;
import com.fasterxml.jackson.annotation.JsonFormat;
import jwt.setting.config.Role;
import jwt.setting.config.SocialType;
import lombok.*;
import org.apache.catalina.User;


import javax.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

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

    private String goodseulName;

    private String skill;

    private String career;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private Timestamp premiumDate;

    private Integer isPremium;

    private String goodseulProfile;

    @OneToMany(mappedBy = "isGoodseul")
    private List<UserEntity> users;
}