package data.entity;

import com.sun.istack.NotNull;
import lombok.*;

import javax.persistence.*;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Entity(name = "chatRoom")
public class ChatRoomEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chatRoomIdx")
    private int chatRoomIdx;

    @Column(name = "roomId")
    private String roomId;

    @Builder
    public ChatRoomEntity(String roomId){
        this.roomId = roomId;
    }

}
