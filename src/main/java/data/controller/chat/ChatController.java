package data.controller.chat;

import com.fasterxml.jackson.databind.JsonNode;
import data.dto.ChatDto;
import data.dto.ChatResponseDto;
import data.service.ChatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.List;

@RestController
@CrossOrigin
@Slf4j
@RequestMapping("/api/lv1/chat")
public class ChatController {
    private final ChatService chatService;
    private final SimpMessageSendingOperations simpMessageSendingOperations;

    public ChatController(ChatService chatService, SimpMessageSendingOperations simpMessageSendingOperations) {
        this.chatService = chatService;
        this.simpMessageSendingOperations = simpMessageSendingOperations;
    }

    @EventListener
    public void onDisconnect(SessionDisconnectEvent e) {
        chatService.handleSessionDisconnect(e);
    }

    @PostMapping("/room")
    public String createRoom(@RequestBody JsonNode jsonNode) {
        return chatService.createChatRoom(jsonNode.get("person1").asInt(),jsonNode.get("person2").asInt());
    }

    @GetMapping
    public ResponseEntity<List<ChatResponseDto>> getAllChats(@RequestParam String roomId) {
        return new ResponseEntity<>(chatService.getAllMessages(roomId), HttpStatus.OK);
    }

    @MessageMapping("/message")
    public void message(ChatDto chatDto, StompHeaderAccessor headerAccessor) {
        chatService.handleMessage(chatDto,headerAccessor);
    }



}
