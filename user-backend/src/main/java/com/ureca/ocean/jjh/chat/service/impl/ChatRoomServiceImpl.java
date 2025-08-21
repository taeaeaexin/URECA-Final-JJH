package com.ureca.ocean.jjh.chat.service.impl;

import com.ureca.ocean.jjh.chat.dto.ChatMessageDto;
import com.ureca.ocean.jjh.chat.dto.ChatRoomMessageResponseDto;
import com.ureca.ocean.jjh.chat.dto.ChatRoomResponseDto;
import com.ureca.ocean.jjh.chat.entity.ChatRoom;
import com.ureca.ocean.jjh.chat.entity.ChatRoomUser;
import com.ureca.ocean.jjh.chat.repository.ChatMessageRepository;
import com.ureca.ocean.jjh.chat.repository.ChatRoomRepository;
import com.ureca.ocean.jjh.chat.repository.ChatRoomUserRepository;
import com.ureca.ocean.jjh.chat.service.ChatRoomService;
import com.ureca.ocean.jjh.community.entity.Post;
import com.ureca.ocean.jjh.community.repository.PostRepository;
import com.ureca.ocean.jjh.exception.ErrorCode;
import com.ureca.ocean.jjh.exception.UserException;
import com.ureca.ocean.jjh.user.entity.User;
import com.ureca.ocean.jjh.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatRoomServiceImpl implements ChatRoomService {
    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final ChatRoomUserRepository chatRoomUserRepository;
    private final ChatMessageRepository chatMessageRepository;
    @Transactional
    @Override
    public ChatRoomResponseDto insertChatRoom(String email, UUID postId) {

        // 로그인한 사용자 조회
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserException(ErrorCode.NOT_FOUND_USER));

        //채팅방이 할당될 글
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new UserException(ErrorCode.POST_NOT_FOUND));

        //상대방 사용자
        User author = post.getAuthor();

        List<ChatRoom> chatRoomsBetweenMeAndOther = chatRoomUserRepository.findCommonChatRooms(user,author);
        if(chatRoomsBetweenMeAndOther.isEmpty()){
            log.info("채팅방이 새로 만들어집니다.");
        }else{
            log.info("같은 사용자 간의 채팅방이 존재합니다. 그 채팅방의 POSTID도 동일한지 확인합니다.");
            for(ChatRoom chatRoom:chatRoomsBetweenMeAndOther){
                Post postCheck = chatRoom.getPost();
                log.info("chatRoom정보 입니다 :" + chatRoom.toString());
                log.info("각각의 postid입니다. 첫번째는 새로 생성하는 post , 뒤는 기존 사용자 간의 post" + post.getId() + ", " + postCheck.getId());
                if(post.getId() == postCheck.getId()){
                    return ChatRoomResponseDto.from(chatRoom,user,author,post);
                }
            }
            log.info("같은 채팅방은 있지만, post가 다릅니다. 채팅방이 새로 만들어집니다.");
        }

        ChatRoom chatRoom = ChatRoom.builder()
                .post(post)
                .build();

        ChatRoom newChatRoom = chatRoomRepository.save(chatRoom);


        ChatRoomUser me = chatRoomUserRepository.save(ChatRoomUser.builder().user(user).chatRoom(newChatRoom).build());
        ChatRoomUser authorChatRoomUser = chatRoomUserRepository.save(ChatRoomUser.builder().user(author).chatRoom(newChatRoom).build());

        return ChatRoomResponseDto.from(newChatRoom,me.getUser(),authorChatRoomUser.getUser(),post);
    }

    @Override
    public List<ChatRoomMessageResponseDto> getChatRoomMessages(UUID chatRoomId){
        List<ChatRoomMessageResponseDto> chatMessageDtoWithNameList = new ArrayList<>();
        for(ChatMessageDto chatMessageDto:  chatMessageRepository.findByChatRoomIdOrderByTimeDesc(chatRoomId)){
            String userName = userRepository.findById(chatMessageDto.getUserId()).get().getName();
            chatMessageDtoWithNameList.add(
                    ChatRoomMessageResponseDto.builder()
                        .userName(userName)
                        .message(chatMessageDto.getMessage())
                        .time(chatMessageDto.getTime())
                        .build()
            );
        }
        return chatMessageDtoWithNameList;
    }

    public List<ChatRoomResponseDto> getChatRoom(String email){
        
        //현재 로그인한 나 찾기
        User me = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserException(ErrorCode.NOT_FOUND_USER));
        
        log.info("로그인 사용자 정보 가져오기");
        //나와 chatting방 목록들의 관계사항 찾아오기
        List<ChatRoomUser> chatRoomUsers = chatRoomUserRepository.findByUser(me);
        log.info(chatRoomUsers.get(0).toString() + " " + chatRoomUsers.get(0).toString());
        //관계사항에서 가공해야할 채팅방 목록 뽑기
        List<ChatRoom> chatRoomList = new ArrayList<>();
        for(ChatRoomUser chatRoomUser : chatRoomUsers){
            chatRoomList.add(chatRoomRepository.findById(chatRoomUser.getChatRoom().getId()).orElseThrow(()-> new UserException(ErrorCode.NOT_FOUND_CHATROOM)));
        }
        
        //채팅방 목록에서 상대방을 구한 후에 dto로 변환하기
        List<ChatRoomResponseDto> chatRoomResponseDtoList = new ArrayList<>();
        for( ChatRoom chatRoom : chatRoomList){
            //채팅방에 소속돼있지만, 내가 아닌 사용자 즉, 상대방이 누구인지 추출
            ChatRoomUser other = chatRoomUserRepository.findByChatRoomButNotMe(chatRoom, me).orElseThrow(()-> new UserException(ErrorCode.NOT_FOUND_USER));
            chatRoomResponseDtoList.add(
                    ChatRoomResponseDto.from(chatRoom,me,other.getUser(),chatRoom.getPost())
                    );
        }

        return chatRoomResponseDtoList;
    }

    @Override
    public void deleteChatRoom(String email, UUID chatRoomId){
        User me = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserException(ErrorCode.NOT_FOUND_USER));

        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow(() -> new UserException(ErrorCode.NOT_FOUND_CHATROOM));
        ChatRoomUser chatRoomUser = chatRoomUserRepository.findByChatRoomAndUser(chatRoom, me);
        if(!chatRoom.getChatRoomUsers().contains(chatRoomUser)){
            log.info("삭제하려는 사용자가 해당 채팅방과 관계 없습니다. 삭제를 금지합니다.");
            throw new UserException(ErrorCode.NOT_AUTHORIZED);
        }
        chatRoomRepository.deleteById(chatRoomId);
        log.info("삭제 완료 : " + chatRoomId);
    }



}
