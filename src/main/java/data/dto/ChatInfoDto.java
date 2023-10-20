package data.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ChatInfoDto {
    private String nickname;
    private long userIdx;
    private long isGoodseul;
}
