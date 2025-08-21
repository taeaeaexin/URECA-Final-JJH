package com.ureca.ocean.jjh.chat.repository;

import com.ureca.ocean.jjh.chat.dto.ChatMessageDto;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ChatMessageRepository extends MongoRepository<ChatMessageDto, String> {
    List<ChatMessageDto> findByChatRoomIdOrderByTimeDesc(UUID chatRoomId);

}
