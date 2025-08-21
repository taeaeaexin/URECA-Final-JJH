package com.ureca.ocean.jjh.chat.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter @Setter
public class ChatRoomRequestDto {

    @Schema(description="약속이 만들어질 post ",example = "4f2f5a23-42da-496e-a50e-d2b20cfb698a")
    private UUID postId;


}
