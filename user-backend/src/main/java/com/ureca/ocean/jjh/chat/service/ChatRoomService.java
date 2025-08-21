package com.ureca.ocean.jjh.chat.service;

import com.ureca.ocean.jjh.chat.dto.ChatRoomMessageResponseDto;
import com.ureca.ocean.jjh.chat.dto.ChatRoomResponseDto;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.UUID;

public interface ChatRoomService {
    @Transactional
    ChatRoomResponseDto insertChatRoom(String email, UUID postId);

    List<ChatRoomMessageResponseDto> getChatRoomMessages(UUID chatRoomId);

    void deleteChatRoom(String email, UUID chatRoomId);
}
