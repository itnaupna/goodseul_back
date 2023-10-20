package data.controller.chat;

import com.fasterxml.jackson.databind.JsonNode;
import data.dto.ChatDto;
import data.dto.ChatInfoDto;
import data.dto.ChatResponseDto;
import data.service.ChatService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@CrossOrigin
@Slf4j
@RequestMapping("/api/lv1/chat")
@Api(tags = "채팅 API")
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
    @ApiOperation(value = "채팅방 생성 API", notes = "두 사용자를 기반으로 채팅방을 생성합니다.")
    public String createRoom(
            @ApiParam(value = "Json body containing the two participants' ids", required = true) @RequestBody JsonNode jsonNode) {
        return chatService.createChatRoom(jsonNode.get("person1").asInt(), jsonNode.get("person2").asInt());
    }

    @GetMapping
    @ApiOperation(value = "채팅 내용 조회 API", notes = "특정 채팅방의 채팅 내용을 조회합니다.")
    public ResponseEntity<List<ChatResponseDto>> getAllChats(
            @ApiParam(value = "ID of the chat room", required = true) @RequestParam String roomId,
            @ApiParam(value = "페이지 번호", defaultValue = "0") @RequestParam(defaultValue = "0") int page) {
        return new ResponseEntity<>(chatService.getAllMessages(roomId,page), HttpStatus.OK);
    }

    @GetMapping("/room")
    @ApiOperation(value = "참여중인 채팅방 조회 API", notes = "로그인한 사용자가 참여중인 채팅방의 목록을 조회합니다.")
    public ResponseEntity<List<ChatInfoDto>> getMyChatRooms(
            @ApiParam(value = "HttpServletRequest object", required = true) HttpServletRequest request)
    {
        return new ResponseEntity<>(chatService.getAllRooms(request),HttpStatus.OK);
    }

    @MessageMapping("/message")
    @ApiOperation(value = "메시지 전송 API", notes = "채팅방에 메시지를 전송합니다.")
    public void message(
            @ApiParam(value = "Chat message details", required = true) ChatDto chatDto,
            @ApiParam(value = "STOMP header accessor for the message", required = true) StompHeaderAccessor headerAccessor) {
        chatService.handleMessage(chatDto, headerAccessor);
    }

}
