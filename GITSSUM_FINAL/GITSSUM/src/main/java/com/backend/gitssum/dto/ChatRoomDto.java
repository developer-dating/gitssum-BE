package com.backend.gitssum.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ChatRoomDto {
    private String roomName;
    private ChatUserDto user;
    private LastMessageDto lastMessage;
    private String lastMessageTime;
    private int notReadingMessageCount;
}