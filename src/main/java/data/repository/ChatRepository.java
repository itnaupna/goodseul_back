package data.repository;

import data.entity.ChatEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface ChatRepository extends JpaRepository<ChatEntity,Integer> {
    List<ChatEntity> findAllByReceiverAndReadCheck(int receiver, boolean check);
    Page<ChatEntity> findAllByRoomId(String roomId,Pageable pageable);
    Page<ChatEntity> findByRoomIdOrderBySendTimeDesc(String roomId, Pageable pageable);
}
