package com.ureca.ocean.jjh.chat.entity;

import com.ureca.ocean.jjh.chat.entity.id.ChatRoomUserId;
import com.ureca.ocean.jjh.user.entity.User;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.ManyToOne;
import lombok.*;

import java.util.UUID;

@Entity
@Builder
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@IdClass(ChatRoomUserId.class)
public class ChatRoomUser {
    @Id
    @ManyToOne
    User user;

    @Id
    @ManyToOne
    ChatRoom chatRoom;
}
