package data.dto;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class GoodseulDto {
    private Long idx;
    private String email;
    private String password;
    private String phoneNumber;
    private String goodseulName;
    private String birth;
    private String location;
    private String skill;
    private String career;
    private String goodseulProfile;
}
