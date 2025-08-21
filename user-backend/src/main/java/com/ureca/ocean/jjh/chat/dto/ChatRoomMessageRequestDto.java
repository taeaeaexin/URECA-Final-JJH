package com.ureca.ocean.jjh.chat.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter @Setter
public class ChatRoomMessageRequestDto {

    @Schema(description="가져올 채팅방 id")
    private UUID chatRoomId;


}
