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

    public enum MessageType {
        ENTER, TALK, EXIT
    }

    private int sender;
    private int receiver;
    private String message;
    private MessageType type;
    private Timestamp time;


    public ChatEntity convertToEntity(ChatDto chatDto) {
        return new ChatEntity(chatDto.getSender(), chatDto.getReceiver(), chatDto.getMessage(), chatDto.getTime());
    }

}
