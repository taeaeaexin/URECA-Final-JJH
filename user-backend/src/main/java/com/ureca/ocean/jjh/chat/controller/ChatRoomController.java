package com.ureca.ocean.jjh.chat.controller;

import com.ureca.ocean.jjh.chat.dto.*;
import com.ureca.ocean.jjh.chat.service.impl.ChatRoomServiceImpl;
import com.ureca.ocean.jjh.common.BaseResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/user/chatRoom")
@RequiredArgsConstructor
@Slf4j
public class ChatRoomController {

    private final ChatRoomServiceImpl chatRoomService;

    @Operation(summary = "채팅방 생성", description = "postId를 기반으로 새로운 채팅방을 생성합니다.")
    @PostMapping
    public ResponseEntity<BaseResponseDto<?>> insertChatRoom(
            @Parameter(hidden = true, description = "현재 로그인한 사용자의 이메일 (헤더에서 전달)") @RequestHeader("X-User-email") String encodedEmail,
            @RequestBody ChatRoomRequestDto chatRoomRequestDto) {
        String email = URLDecoder.decode(encodedEmail, StandardCharsets.UTF_8);
        log.info("user-backend 내의 current userEmail : " + email);
        ChatRoomResponseDto ChatRoomResponseDto = chatRoomService.insertChatRoom(email, chatRoomRequestDto.getPostId());
        return ResponseEntity.ok(BaseResponseDto.success(ChatRoomResponseDto));
    }

    @Operation(summary = "채팅방 메시지 조회", description = "채팅방 ID를 통해 해당 채팅방의 메시지 목록을 조회합니다.")
    @GetMapping("/messages")
    public ResponseEntity<BaseResponseDto<?>> getChatRoomMessages(
            @Parameter(description = "조회할 채팅방의 ID") @RequestParam UUID chatRoomId) {
        List<ChatRoomMessageResponseDto> ChatMessageDto = chatRoomService.getChatRoomMessages(chatRoomId);
        return ResponseEntity.ok(BaseResponseDto.success(ChatMessageDto));
    }

    @Operation(summary = "내 채팅방 목록 조회", description = "현재 로그인한 사용자가 참여 중인 모든 채팅방을 조회합니다.")
    @GetMapping
    public ResponseEntity<BaseResponseDto<?>> getMyChatRooms(
            @Parameter(hidden = true, description = "현재 로그인한 사용자의 이메일 (헤더에서 전달)") @RequestHeader("X-User-email") String encodedEmail
    ) {
        String email = URLDecoder.decode(encodedEmail, StandardCharsets.UTF_8);
        log.info("user-backend 내의 current userEmail : " + email);
        List<ChatRoomResponseDto> chatRoomResponseDtoList = chatRoomService.getChatRoom(email);
        return ResponseEntity.ok(BaseResponseDto.success(chatRoomResponseDtoList));
    }

    @Operation(summary = "채팅방 삭제", description = "채팅방을 삭제합니다.")
    @DeleteMapping
    public ResponseEntity<BaseResponseDto<?>> deleteChatRoom(
            @Parameter(hidden = true, description = "현재 로그인한 사용자의 이메일 (헤더에서 전달)") @RequestHeader("X-User-email") String encodedEmail,
            @Parameter(description = "삭제할 채팅방의 ID") @RequestParam UUID chatRoomId
    ) {
        String email = URLDecoder.decode(encodedEmail, StandardCharsets.UTF_8);
        log.info("user-backend 내의 current userEmail : " + email);
        chatRoomService.deleteChatRoom(email, chatRoomId);
        return ResponseEntity.ok(BaseResponseDto.success(Void.class));
    }


}
