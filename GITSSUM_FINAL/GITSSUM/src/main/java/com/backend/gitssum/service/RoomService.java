package com.backend.gitssum.service;


import com.backend.gitssum.dto.ChatRoomDto;
import com.backend.gitssum.dto.ChatUserDto;
import com.backend.gitssum.dto.LastMessageDto;
import com.backend.gitssum.dto.RoomDto;
import com.backend.gitssum.entity.Message;
import com.backend.gitssum.entity.Room;
import com.backend.gitssum.entity.UserRoom;
import com.backend.gitssum.repository.MessageRepository;
import com.backend.gitssum.repository.RoomRepository;
import com.backend.gitssum.repository.UserRoomRepository;
import com.backend.gitssum.entity.MessageTimeConversion;
import com.backend.gitssum.entity.ImageFile;
import com.backend.gitssum.entity.User;
import com.backend.gitssum.exception.CustomException;
import com.backend.gitssum.exception.ErrorCode;
import com.backend.gitssum.repository.LikeRepository;
import com.backend.gitssum.repository.UserRepository;
import com.backend.gitssum.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoomService {
    private final UserRepository userRepository;
    private final RoomRepository roomRepository;
    private final UserRoomRepository userRoomRepository;
    private final MessageRepository messageRepository;
    private final LikeRepository likeRepository;

    @Transactional
    public RoomDto.Response createRoomService(RoomDto.Request roomDto, UserDetailsImpl userDetails) throws MessagingException {
        //메시지를 하는 유저 (로그인한 유저)
        User user = userDetails.getUser();
        // 메시지를 받을 유저
        User toUser = userRepository.findById(roomDto.getToUserId()).orElseThrow( //게시물 주인
                () -> new CustomException(ErrorCode.USER_NOT_FOUND)
        );
        likeRepository.deleteByLikedUserIdAndLikerUserId(user.getId(), toUser.getId());
        likeRepository.deleteByLikedUserIdAndLikerUserId(toUser.getId(), user.getId());
        // 방 정보 생성
        String roomName = UUID.randomUUID().toString();

        // 방 생성
        Room room = Room.builder()
                .roomName(roomName)
                .build();
        //room db에 저장
        roomRepository.save(room);

        //메시지를 걸은 사람의 입장에서 보이는 채팅방 생성
        UserRoom userRoom = UserRoom.builder()
                .room(room)
                .user(user)
                .toUser(toUser)
                .lastMessageId(null)
                .build();
        userRoomRepository.save(userRoom);

        // 메시지를 받는 유저 입장에서 보이는 채팅방 생성
        UserRoom toUserRoom = UserRoom.builder()
                .room(room)
                .user(toUser)
                .toUser(user)
                .lastMessageId(null)
                .build();
        userRoomRepository.save(toUserRoom);

        //메시지를 받는 유저의 정보를 담는 Dto
        List<String> imageFileList = new ArrayList<>();
        for (ImageFile imageFile : user.getImageFileList()) {
            imageFileList.add(imageFile.getPath());
        }
        ChatUserDto chatUserDto = ChatUserDto.builder()
                .userId(toUser.getId())
                .imageList(imageFileList)
                .nickname(toUser.getUsername())
                .build();

        RoomDto.Response response = RoomDto.Response.builder()
                .roomName(room.getRoomName()) //현재 방
                .user(chatUserDto) //메시지를 받는 유저
                .build();

        return response;
    }

    @Transactional
    public List<ChatRoomDto> showRoomListService(UserDetailsImpl userDetails) {
        //현재 로그인한 유저의 채팅방 목록 리스트 뽑기
        List<UserRoom> userRooms = userRoomRepository.findByUser(userDetails.getUser());
        //반환할 리스트 만들기
        List<ChatRoomDto> chatRoomDtos = new ArrayList<>();
        //반복문
        for (UserRoom userRoom : userRooms) {
            //마지막 메시지&시간
            LastMessageDto lastMessageDto;

            //채팅 상대들(toUser )의 정보
            //imageFileList
            List<String> imageFileList = new ArrayList<>();
            for (ImageFile imageFile : userRoom.getToUser().getImageFileList()) {
                imageFileList.add(imageFile.getPath());
            }
            ChatUserDto chatUserDto = ChatUserDto.builder()
                    .userId(userRoom.getToUser().getId()) //상대편 유저 pk
                    .imageList(imageFileList)
                    .nickname(userRoom.getToUser().getUsername())
                    .build();

            //채팅방리스트에 보여주는 마지막 메시지 값
            if (userRoom.getLastMessageId() == null) {//마지막 메시지 없는 경우
                lastMessageDto = LastMessageDto.builder()
                        .content("채팅방이 생성되었습니다.") //채팅방 생성 메시지와 시간
                        .createdAt(MessageTimeConversion.timeConversion(userRoom.getCreatedAt()))
                        .roomCreatedAt(userRoom.getCreatedAt().toString())
                        .build();
            } else {
                //마지막 메시지 존재하는 경우니까 메시지 정보 갖고 오기
                Message message = messageRepository.findById(userRoom.getLastMessageId()).orElseThrow(
                        () -> new CustomException(ErrorCode.LAST_MESSAGE_NOT_FOUND)
                );
                //마지막 메시지 dto에 메시지 정보 담아주기
                lastMessageDto = LastMessageDto.builder()
                        .content(message.getContent())
                        .createdAt(MessageTimeConversion.timeConversion(message.getCreatedAt()))
                        .roomCreatedAt(userRoom.getCreatedAt().toString())
                        .build();


            }
            ChatRoomDto chatRoomDto;
            chatRoomDto = ChatRoomDto.builder()
                    .roomName(userRoom.getRoom().getRoomName())
                    .user(chatUserDto)
                    .lastMessage(lastMessageDto)
                    .lastMessageTime(lastMessageDto.getCreatedAt())
                    .build();
            chatRoomDtos.add(chatRoomDto);
        }
        return chatRoomDtos.stream().sorted(Comparator.comparing(ChatRoomDto::getLastMessageTime).reversed())
                .collect(Collectors.toList());
    }
}
