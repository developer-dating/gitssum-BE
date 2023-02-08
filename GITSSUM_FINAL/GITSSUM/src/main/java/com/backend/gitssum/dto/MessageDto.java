package com.backend.gitssum.dto;

import com.backend.gitssum.entity.Message;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class MessageDto {

    private Message.MessageType type;
    private String roomName;
    private Long senderId;
    private String message;
    private Long receiverId;
    private String createdAt;
}
