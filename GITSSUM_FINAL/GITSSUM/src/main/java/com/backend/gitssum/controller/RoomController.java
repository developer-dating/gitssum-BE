package com.backend.gitssum.controller;

import com.backend.gitssum.dto.ChatRoomDto;
import com.backend.gitssum.dto.RoomDto;
import com.backend.gitssum.service.RoomService;
import com.backend.gitssum.redis.RedisMessageSubscriber;
import com.backend.gitssum.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class RoomController {
    private final RedisMessageSubscriber redisMessageSubscriber;
    private final RedisMessageListenerContainer redisMessageListenerContainer;
    private final Map<String, ChannelTopic> topics;
    private final RoomService roomService;

    // 방 생성 api : 채팅 상대의 userId를 받아옴
    @PostMapping("/api/room")
    public RoomDto.Response roomCreate(@RequestBody RoomDto.Request roomDto,
                                       @AuthenticationPrincipal UserDetailsImpl userDetails) throws MessagingException, javax.mail.MessagingException {
        return roomService.createRoomService(roomDto, userDetails);
    }

    //채팅방 리스트 api -> 현재 유저의 방생성 목록
    @GetMapping("/api/room")
    public List<ChatRoomDto> showRoomList(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return roomService.showRoomListService(userDetails);

    }


    //채팅방 참가
    @MessageMapping("/join")
    public void join(String roomId) {
        String roomId2 = roomId.replaceAll("\"", "");
        ChannelTopic topic = topics.get(roomId2);
        if (topic == null) {
            topic = new ChannelTopic(roomId2);
            //채팅방 접속 경우 해당 채팅방을 구독한다는 정보를 redisMessageListener에 등록
            redisMessageListenerContainer.addMessageListener(redisMessageSubscriber, topic);
        }
    }


    }
