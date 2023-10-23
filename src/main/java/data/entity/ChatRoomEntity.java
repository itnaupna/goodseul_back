package data.entity;

import com.sun.istack.NotNull;
import lombok.*;

import javax.persistence.*;
import java.sql.Timestamp;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Entity(name = "chatRoom")
public class ChatRoomEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int chatRoomIdx;

    private String roomId;

    private Timestamp lastChatTime;
}
