package com.backend.gitssum.service;

import com.backend.gitssum.dto.MessageDto;
import com.backend.gitssum.dto.MessageListDto;
import com.backend.gitssum.dto.RoomDto;
import com.backend.gitssum.entity.Message;
import com.backend.gitssum.entity.Room;
import com.backend.gitssum.entity.UserRoom;
import com.backend.gitssum.repository.MessageRepository;
import com.backend.gitssum.repository.RoomRepository;
import com.backend.gitssum.repository.UserRoomRepository;
import com.backend.gitssum.entity.MessageTimeConversion;
import com.backend.gitssum.entity.User;
import com.backend.gitssum.exception.CustomException;
import com.backend.gitssum.exception.ErrorCode;
import com.backend.gitssum.redis.RedisMessagePublisher;
import com.backend.gitssum.repository.UserRepository;
import com.backend.gitssum.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional
public class MessageService {
    private final RedisMessagePublisher redisMessagePublisher;
    private final UserRepository userRepository;
    private final RoomRepository roomRepository;
    private final UserRoomRepository userRoomRepository;
    private final MessageRepository messageRepository;
    public void sendMessage(MessageDto messageDto){
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
        MessageDto sendMessageDto = new MessageDto();
        boolean check = false;
        User sender = userRepository.findById(messageDto.getSenderId()).orElseThrow(
                () -> new CustomException(ErrorCode.USER_NOT_FOUND)
        );
        //전달 메시지 타입 체크
        //메시지 시작
        if(Message.MessageType.START.equals(messageDto.getType())){
            sendMessageDto = MessageDto.builder()
                    .message(sender.getUsername()+"님이 입장하셨습니다.")
                    .senderId(sender.getId())
                    .roomName(messageDto.getRoomName())
                    .receiverId(messageDto.getReceiverId())
                    .createdAt(MessageTimeConversion.timeConversion(now))
                    .type(messageDto.getType())
                    .build();
            //채팅 나갈 시 메시지
        }else if(Message.MessageType.EXIT.equals(messageDto.getType())){
            sendMessageDto = MessageDto.builder()
                    .message(sender.getUsername()+ "님이 퇴장하셨습니다.")
                    .senderId(sender.getId())
                    .roomName(messageDto.getRoomName())
                    .createdAt(MessageTimeConversion.timeConversion(now))
                    .type(messageDto.getType())
                    .receiverId(messageDto.getReceiverId())
                    .build();

            check = roomOut(sendMessageDto); // roomOut ->true 반환 시

            //채팅
        }else if(Message.MessageType.TALK.equals(messageDto.getType())){
            sendMessageDto = MessageDto.builder()
                    .message(messageDto.getMessage())
                    .senderId(sender.getId())
                    .roomName(messageDto.getRoomName())
                    .createdAt(MessageTimeConversion.timeConversion(now))
                    .type(messageDto.getType())
                    .receiverId(messageDto.getReceiverId())
                    .build();
        }

        if(!check){
            // TALK / START
            Room room = roomRepository.findByRoomName(sendMessageDto.getRoomName()).orElseThrow(
                    () -> new CustomException(ErrorCode.NOT_EXIST_ROOM)
            );

            //방을 공유하는 유저룸 리스트 갖고 오기
            List<UserRoom> userRoomList = userRoomRepository.findByRoom(room);

            // 메시지/타입/sender/게시글 작성자 정보 + 유저와 방 repo = 해당 메시지를 저장
            Message message = new Message(sendMessageDto, userRepository, roomRepository);
            messageRepository.save(message);

            for(UserRoom userRoom : userRoomList){
                //메시지를 보내는 pk 를 두 개의 userRoom의 마지막 메시지를 보낸 Id로 변경
                userRoom.lastMessageIdChange(message.getId());

            }

            // pub -> 채널 구독자에게 전달
            redisMessagePublisher.publish(sendMessageDto);
        }
    }

    //유저가 방을 나갈 때
    public boolean roomOut(MessageDto sendMessageDto){
        //현재 방 찾기
        Room room = roomRepository.findByRoomName(sendMessageDto.getRoomName()).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_EXIST_ROOM)
        );
        //방 찾고 유저룸 리스트 반환
        List<UserRoom> userRoomList = userRoomRepository.findByRoom(room);

        //현재 유저룸 리스트에서 자신의 유저룸 삭제
        for(UserRoom userRoom : userRoomList){
            //현재 메시지를 보내려는 유저와 해당 방 userRoom의 pk값 같으면 삭제
            if(userRoom.getUser().getId() == sendMessageDto.getSenderId()){
                userRoomRepository.deleteById(userRoom.getId());
            }
        }
        //메시지를 받는 유저룸에서는 메시지를 삭제하고, 방정보도 삭제
        if(userRoomList.size() == 1 ){
            messageRepository.deleteAllByRoom(room);
            roomRepository.deleteById(room.getId());
            return true;
        }
        return false;
    }

    //방의 메시지 리스트 조회 /
    @Transactional
    public MessageListDto showMessageList(RoomDto.findRoomDto roomDto, Pageable pageable, UserDetailsImpl userDetails){
        Room room = roomRepository.findByRoomName(roomDto.getRoomName()).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_EXIST_ROOM));

        //특정 방에 해당하는 메시지 정보 가져오기
        PageImpl<Message> messages = messageRepository.findByRoom(room,pageable);

        //메시지를 리스트에 담아주기 [나와 상대방의 채팅 리스트 나누기]
        List<MessageDto> messageDtos = DiscriminationWhoSentMessage(roomDto, userDetails, room, messages);

        // 메시지가 담긴 리스트 반환 ,
        return MessageListDto.builder()
                .message(messageDtos)
                .build();
    }

    private List<MessageDto> DiscriminationWhoSentMessage(RoomDto.findRoomDto roomDto , UserDetailsImpl userDetails
            ,Room room, PageImpl<Message> messages){
        List<MessageDto> messageDtos = new ArrayList<>();
        for(Message message: messages){
            long receiverId = 0L;
            //상대방이 보낸 메시지
            if(roomDto.getToUserId() == message.getUser().getId()){
                receiverId = userDetails.getUser().getId();
            }else{
                receiverId = roomDto.getToUserId();
            }
            //게시물에 접근한 유저가 보낸 메시지
            MessageDto messageDto = MessageDto.builder()
                    .message(message.getContent())
                    .roomName(room.getRoomName())
                    .senderId(message.getUser().getId())
                    .receiverId(receiverId)
                    .type(message.getMessageType())
                    .createdAt(MessageTimeConversion.timeConversion(message.getCreatedAt()))
                    .build();
            messageDtos.add(messageDto);
        }
        return messageDtos;
    }

}
