//package data.config;
//
//import jwt.setting.settings.JwtService;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.messaging.Message;
//import org.springframework.messaging.MessageChannel;
//import org.springframework.messaging.simp.stomp.StompCommand;
//import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
//import org.springframework.messaging.support.ChannelInterceptor;
//import org.springframework.stereotype.Component;
//
//@Slf4j
//@Component
//public class WebSocketHandler implements ChannelInterceptor {
//
//    private final JwtService jwtService;
//
//    public WebSocketHandler(JwtService jwtService) {
//        this.jwtService = jwtService;
//    }
//
//    @Override
//    public Message<?> preSend(Message<?> message, MessageChannel channel) {
//        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
//        // webSocket 연결시 헤더의 jwt token 검증.
//        if (StompCommand.CONNECT == accessor.getCommand()) {
//            jwtService.isTokenValid(accessor.getFirstNativeHeader("Authorization"));
//        }
//
//        return message;
//    }
//}
