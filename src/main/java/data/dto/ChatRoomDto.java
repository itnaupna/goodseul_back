package data.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ChatRoomDto {
    private String roomId;
    private String sessionId;
    private int userIdx;
    @Builder
    public ChatRoomDto (String roomId,String sessionId, int userIdx) {
        this.roomId = roomId;
        this.sessionId = sessionId;
        this.userIdx = userIdx;
    }

}
