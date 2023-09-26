package data.service;

import data.dto.ChatDto;
import data.entity.ChatEntity;
import data.entity.ChatRoomEntity;
import data.repository.ChatRepository;
import data.repository.ChatRoomRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatService {

    private final ChatRepository chatRepository;
    private final ChatRoomRepository chatRoomRepository;

    public ChatService(ChatRepository chatRepository, ChatRoomRepository chatRoomRepository) {
        this.chatRepository = chatRepository;
        this.chatRoomRepository = chatRoomRepository;
    }

    public String createChatRoom (int person1, int person2) {
        ChatRoomEntity chatRoomEntity = new ChatRoomEntity();

        if(person1 > person2) {
            chatRoomEntity.setRoomId(person2 + "to" + person1);
        } else {
            chatRoomEntity.setRoomId(person1 + "to" + person2);
        }

        chatRoomRepository.save(chatRoomEntity);
        return chatRoomEntity.getRoomId();
    }

    public void saveChat (ChatDto chatDto) {
        chatRepository.save(chatDto.convertToEntity(chatDto));
    }

    public List<ChatEntity> getAllMessages(int sender, int receiver) {
        return chatRepository.findAllBySenderAndReceiver(sender,receiver);
    }
    public void updateReadCheck(int receiver) {
        List<ChatEntity> unReadMessages = chatRepository.findAllByReceiverAndReadCheck(receiver,false);
        for(ChatEntity chatEntity : unReadMessages) {
            chatEntity.setReadCheck(true);
            chatRepository.save(chatEntity);
        }
    }

}
