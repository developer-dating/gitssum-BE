package com.backend.gitssum.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class ChatUserDto {
    private String nickname;
    private List<String> imageList;
    private Long userId;
}
