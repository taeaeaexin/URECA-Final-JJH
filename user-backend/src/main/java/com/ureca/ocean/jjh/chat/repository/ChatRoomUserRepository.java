package com.ureca.ocean.jjh.chat.repository;

import com.ureca.ocean.jjh.chat.entity.ChatRoom;
import com.ureca.ocean.jjh.chat.entity.ChatRoomUser;
import com.ureca.ocean.jjh.chat.entity.id.ChatRoomUserId;

import com.ureca.ocean.jjh.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ChatRoomUserRepository  extends JpaRepository<ChatRoomUser, ChatRoomUserId> {
    List<ChatRoomUser> findByUser(User user);

    @Query("SELECT c FROM ChatRoomUser c WHERE c.user != :me AND c.chatRoom = :chatRoom")
    Optional<ChatRoomUser> findByChatRoomButNotMe(ChatRoom chatRoom, User me);

//    @Query("SELECT DISTINCT c.chatRoom FROM ChatRoomUser c1 JOIN ChatRoomUser c2 WHERE (c1.user =:me  OR c2.user =:other))
//    List<ChatRoom> findByParticipants(User me, User other);
    @Query("""
        SELECT cru1.chatRoom
        FROM ChatRoomUser cru1
        JOIN ChatRoomUser cru2 ON cru1.chatRoom = cru2.chatRoom
        WHERE cru1.user = :me AND cru2.user = :other
    """)
    List<ChatRoom> findCommonChatRooms(User me, User other);

    ChatRoomUser findByChatRoomAndUser(ChatRoom chatRoom, User user);

}
