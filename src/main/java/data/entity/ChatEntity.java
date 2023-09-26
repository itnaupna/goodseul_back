package data.entity;

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

    private String roomId;

    private int sender;

    private int receiver;

    private String message;

    private Timestamp sendTime;

    private boolean readCheck;

    @Builder
    public ChatEntity (int sender, int receiver, String message, Timestamp sendTime) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.sendTime = sendTime;
    }


}
