package com.ureca.ocean.jjh.chat.entity.id;// 1. 복합 키 클래스 정의 (Serializable 필수)
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ChatRoomUserId implements Serializable {
    private UUID user;
    private UUID chatRoom;
}
