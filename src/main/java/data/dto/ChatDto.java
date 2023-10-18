package data.dto;

import data.entity.ChatEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatDto {

    private int sender;
    private int receiver;
    private String message;
    private String type;
    private Timestamp time;
    private String roomId;

    @Builder
    public ChatDto(int sender, int receiver, String message, Timestamp time, String roomId) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.time = time;
        this.roomId = roomId;
    }


    public ChatEntity convertToEntity(ChatDto chatDto) {
        return new ChatEntity(chatDto.getSender(), chatDto.getReceiver(), chatDto.getMessage(), new Timestamp(System.currentTimeMillis()), false, chatDto.getRoomId());
    }

}
