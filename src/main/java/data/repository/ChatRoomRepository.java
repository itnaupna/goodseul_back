package data.repository;

import data.entity.ChatRoomEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoomEntity,Long> {
    public boolean existsByRoomId(String roomId);
    public ChatRoomEntity findByRoomId(String roomId);
    List<ChatRoomEntity> findByRoomIdStartingWithOrRoomIdEndingWithOrderByLastChatTimeDesc(String prefix, String suffix);

}
