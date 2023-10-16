package data.repository;

import data.entity.ChatEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRepository extends JpaRepository<ChatEntity,Integer> {
    List<ChatEntity> findAllByReceiverAndReadCheck(int receiver, boolean check);
    List<ChatEntity> findAllByRoomId(String roomId);
}
