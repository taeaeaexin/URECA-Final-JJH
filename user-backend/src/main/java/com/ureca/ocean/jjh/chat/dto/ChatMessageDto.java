package com.ureca.ocean.jjh.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.UUID;

@Document
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageDto {
    private String message;
    private UUID chatRoomId;
    private UUID userId;
    private LocalDateTime time;
}
