package com.ureca.ocean.jjh.chat.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ureca.ocean.jjh.chat.dto.ChatMessageDto;
import com.ureca.ocean.jjh.chat.repository.ChatMessageRepository;
import com.ureca.ocean.jjh.exception.ErrorCode;
import com.ureca.ocean.jjh.exception.UserException;
import com.ureca.ocean.jjh.user.entity.User;
import com.ureca.ocean.jjh.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.time.LocalDateTime;
@Component
@Slf4j
@RequiredArgsConstructor
public class ChatWebSocketHandler extends TextWebSocketHandler {

    // 채팅방 ID별로 연결된 세션을 관리
    private final Map<UUID, Set<WebSocketSession>> chatRooms = new ConcurrentHashMap<>();
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ChatMessageRepository chatMessageRepository;
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // 연결 초기에는 아무 채팅방에도 소속되지 않음
        String ip = session.getRemoteAddress().getAddress().getHostAddress();
        log.info("새 연결: IP = " + ip + ", Session ID = " + session.getId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> payload = mapper.readValue(message.getPayload(), new TypeReference<>() {});
        String messageContent = payload.get("message");
        String type = payload.get("type");
        UUID roomId = UUID.fromString(payload.get("roomId"));
        UUID userId = UUID.fromString(payload.get("sender"));
        String name = userRepository.findById(userId).orElseThrow(()->new UserException(ErrorCode.NOT_FOUND_USER)).getName();
        log.info("type :" + type );
        if ("join".equals(type)) {
            // 채팅방에 세션만 등록하고 메시지는 보내지 않음
            chatRooms.computeIfAbsent(roomId, k -> ConcurrentHashMap.newKeySet()).add(session);
            log.info(">> " + roomId + "에 " + session.getId() + " 입장");
        } else if ("chat".equals(type)) {
            // 메시지를 해당 채팅방의 모든 세션에 브로드캐스트
            log.info("broad cast");
            chatRooms.computeIfAbsent(roomId, k -> ConcurrentHashMap.newKeySet()).add(session); // 혹시 모르니 추가 ( join 메시지를 보내지 않고, 바로 채팅메시지만 보낼 경우 )
            //채팅 메시지 저장
            ChatMessageDto chatMessageDto = new ChatMessageDto(messageContent,roomId,userId,LocalDateTime.now(ZoneId.of("Asia/Seoul")));
            chatMessageRepository.save(chatMessageDto);
            
            Map<String,String> payloadResponse = new HashMap<>();
            payloadResponse.put("roomId", roomId.toString());
            payloadResponse.put("sender", name);
            payloadResponse.put("message", messageContent);

            String json = objectMapper.writeValueAsString(payloadResponse);
            for (WebSocketSession s : chatRooms.get(roomId)) {
                if (s.isOpen()) {
                    s.sendMessage(new TextMessage(json));
                }
            }
        }
    }


    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        // 세션 제거
        System.out.println("afterConnectionClosed ");
        chatRooms.values().forEach(set -> set.remove(session));
    }
}
