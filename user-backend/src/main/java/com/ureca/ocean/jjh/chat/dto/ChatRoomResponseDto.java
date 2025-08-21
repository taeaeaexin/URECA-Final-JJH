package com.ureca.ocean.jjh.chat.dto;

import com.ureca.ocean.jjh.chat.entity.ChatRoom;
import com.ureca.ocean.jjh.community.dto.response.PostResponseDto;
import com.ureca.ocean.jjh.community.entity.Post;
import com.ureca.ocean.jjh.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Builder
@Getter @Setter
public class ChatRoomResponseDto {

    @Schema(description = "채팅방 ID")
    private UUID chatRoomId;

    @Schema(description = "내 ID")
    private UUID me;

    @Schema(description = "상대방 사용자 ID")
    private UUID other;

    @Schema(description = "상대방 사용자 닉네임")
    private String otherNickname;


    private PostResponseDto postResponseDto;
    public static ChatRoomResponseDto from (ChatRoom chatRoom, User me, User other, Post post){

        return ChatRoomResponseDto.builder()
                .chatRoomId(chatRoom.getId())
                .me(me.getId())
                .other(other.getId())
                .otherNickname(other.getNickname())
                .postResponseDto(PostResponseDto.of(post))
                .build();

    }
}
