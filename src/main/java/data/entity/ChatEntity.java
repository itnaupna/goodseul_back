package data.entity;

import data.dto.ChatDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "chat")
public class ChatEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int chatIdx;

    private int sender;

    private int receiver;

    private String message;

    private Timestamp sendTime;

    private boolean readCheck;

    private String roomId;

    @Builder
    public ChatEntity (int sender, int receiver, String message, Timestamp sendTime, boolean readCheck,String roomId) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.sendTime = sendTime;
        this.readCheck = readCheck;
        this.roomId = roomId;
    }

}
