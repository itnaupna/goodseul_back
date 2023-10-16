package data.service;

import data.dto.ChatDto;
import data.dto.ChatResponseDto;
import data.dto.ChatRoomDto;
import data.entity.ChatEntity;
import data.entity.ChatRoomEntity;
import data.repository.ChatRepository;
import data.repository.ChatRoomRepository;
import jwt.setting.config.Role;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class ChatService {
    private final SimpMessageSendingOperations sendingOperations;
    private final ChatRepository chatRepository;
    private final ChatRoomRepository chatRoomRepository;
    private Map<String, Integer> session = new HashMap<>();
    private Map<String, ChatRoomDto> sessionData = new HashMap<>();

    public ChatService(SimpMessageSendingOperations sendingOperations, ChatRepository chatRepository, ChatRoomRepository chatRoomRepository) {
        this.sendingOperations = sendingOperations;
        this.chatRepository = chatRepository;
        this.chatRoomRepository = chatRoomRepository;
    }

    public String createChatRoom (int person1, int person2) {
        ChatRoomEntity chatRoomEntity = new ChatRoomEntity();
        chatRoomEntity.setRoomId(roomNameCheck(person1,person2));

        if(!chatRoomRepository.existsByRoomId(chatRoomEntity.getRoomId())) {
            chatRoomRepository.save(chatRoomEntity);
        }
        return chatRoomEntity.getRoomId();
    }

    public void saveChat (ChatDto chatDto) {
        String roomId = roomNameCheck(chatDto.getSender(),chatDto.getReceiver());

        if(session.get(roomId) == 2) {
            ChatEntity entity = chatDto.convertToEntity(chatDto);
            entity.setReadCheck(true);
            entity.setRoomId(roomId);
            chatRepository.save(entity);
        } else {
            chatDto.setRoomId(roomId);
            chatRepository.save(chatDto.convertToEntity(chatDto));
        }
    }

    public List<ChatResponseDto> getAllMessages(String roomId) {
        List<ChatEntity> chatEntities = chatRepository.findAllByRoomId(roomId);
        List<ChatResponseDto> chatDtos = new ArrayList<>();

        for(ChatEntity entity : chatEntities) {
            chatDtos.add(new ChatResponseDto(entity.getSender(),entity.getReceiver(),entity.getMessage(),entity.getSendTime(),entity.isReadCheck(),entity.getRoomId()));
        }

        return chatDtos;
    }

    public void updateReadCheck(int receiver) {
        List<ChatEntity> unReadMessages = chatRepository.findAllByReceiverAndReadCheck(receiver,false);
        for(ChatEntity chatEntity : unReadMessages) {
            chatEntity.setReadCheck(true);
            chatRepository.save(chatEntity);
        }
    }

    public void handleSessionDisconnect(SessionDisconnectEvent e) {
        StompHeaderAccessor headers = StompHeaderAccessor.wrap(e.getMessage());
        String sessionId = headers.getSessionId();
        String roomId = sessionData.get(sessionId).getRoomId();

        session.put(roomId,session.get(roomId) == 2 ? 1 : 0);
        ChatRoomDto chatRoomDto = sessionData.get(sessionId);
        sessionData.remove(sessionId);
        ChatDto chatDto = new ChatDto();
        chatDto.setType("EXIT");
        chatDto.setSender(chatRoomDto.getUserIdx());
        chatDto.setMessage(chatRoomDto.getUserIdx() + "님이 퇴장 하셨습니다.");

        sendingOperations.convertAndSend("/sub/"+ roomId,chatDto);

    }

    public void handleMessage(ChatDto chatDto, StompHeaderAccessor headerAccessor) {
        String sessionId = headerAccessor.getSessionId();
        String roomId = roomNameCheck(chatDto.getSender(),chatDto.getReceiver());

        switch (chatDto.getType()) {
            case "ENTER":
                chatDto.setMessage(chatDto.getSender() + "님이 입장하셨습니다.");
                ChatRoomDto chatRoomDto = new ChatRoomDto(roomId,sessionId,chatDto.getSender());
                session.put(roomId, session.get(roomId) == null ? 1 : 2);
                sessionData.put(sessionId,chatRoomDto);

                updateReadCheck(chatDto.getSender());

                log.info("session : " + session.toString());
                log.info("session Data : " + sessionData.toString());
                break;

            case "TALK":
                saveChat(chatDto);
                break;

            case "EXIT":
                break;

            default:
                throw new IllegalArgumentException("Invalid message type:" + chatDto.getType());
        }
        sendingOperations.convertAndSend("/sub/" + roomId, chatDto);
    }

    public String roomNameCheck (int person1, int person2) {
        if(person1 > person2) {
            return person2 + "to" + person1;
        } else {
            return person1 + "to" + person2;
        }
    }



}
