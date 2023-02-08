package com.backend.gitssum.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class LastMessageDto {
    private String content;
    private String createdAt;
    private String roomCreatedAt;
}