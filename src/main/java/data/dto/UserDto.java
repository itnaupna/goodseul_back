package data.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class UserDto {
    private Long idx;
    private String email;
    private String name;
    private String password;
    private String nickname;
    private String phoneNumber;
    private String location;
    private String birth;
    private int isGoodseul;
}
