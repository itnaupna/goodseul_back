package data.controller.chat;

import com.fasterxml.jackson.databind.JsonNode;
import data.dto.ChatDto;
import data.repository.ChatRepository;
import data.repository.ChatRoomRepository;
import data.service.ChatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;

@Slf4j
@Controller
@CrossOrigin
@RequestMapping("/api/lv1/chat")
public class ChatController {

    private final SimpMessageSendingOperations template;
    private final ChatService chatService;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRepository chatRepository;
    private final RedisTemplate redisTemplate;

    public ChatController(SimpMessageSendingOperations template, ChatRepository chatRepository, ChatService chatService, ChatRoomRepository chatRoomRepository, RedisTemplate redisTemplate) {
        this.template = template;
        this.chatService = chatService;
        this.chatRoomRepository = chatRoomRepository;
        this.chatRepository = chatRepository;
        this.redisTemplate = redisTemplate;
    }

    @PostMapping
    public ResponseEntity<String> createRoom(@RequestBody JsonNode json) {
        log.info("사람 1 번호 : " + json.get("person1").asText());
        log.info("사람 2 번호 : " + json.get("person2").asText());
        String chatId = chatService.createChatRoom(json.get("person1").asInt(),json.get("person2").asInt());
        log.info("chat ID : " + chatId);
        return new ResponseEntity<String>( chatId , HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<Boolean> checkRoom(@RequestParam int person1, int person2) {
        return new ResponseEntity<>(chatRoomRepository.existsByRoomId(person1 > person2 ? person2 + "to" + person1 : person1 + "to" + person2) , HttpStatus.OK);
    }

    @PostMapping("/update")
    public ResponseEntity<Void> updateReadCheck(@RequestBody JsonNode json) {
        log.info("Update read check endpoint hit");
        chatService.updateReadCheck(json.get("receiver").asInt());
        return ResponseEntity.ok().build();
    }


    @MessageMapping("/send")
    public ChatDto sendChat(@Payload ChatDto chatDto) {
        // 송신자 발신자 Memeber PrimaryKey
        int sender = chatDto.getSender();
        int receiver = chatDto.getReceiver();

        chatDto.setTime(new Timestamp(System.currentTimeMillis()));
        chatDto.setReadCheck(false);

        log.info(chatDto.toString());

        // 채팅방 명명규칙 : 두사람중 인덱스가 더 작은사람이 앞에 위치
        // ex) 사람1 : 100 , 사람2 : 200 => 100and200
        String roomId = (sender > receiver ? receiver + "to" + sender : sender + "to" + receiver);
        template.convertAndSend("/sub/" + roomId,chatDto);

        chatService.saveChat(chatDto);

        return chatDto;
    }

}
