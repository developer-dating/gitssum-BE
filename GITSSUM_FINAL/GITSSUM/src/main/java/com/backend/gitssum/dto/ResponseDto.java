package com.backend.gitssum.dto;

import lombok.Getter;

@Getter
public class ResponseDto {
    private String msg;
    private int statusCode;
    private Long userId;
    private boolean profile;

    public ResponseDto(String msg, int statusCode) {
        this.msg = msg;
        this.statusCode = statusCode;
    }

    public ResponseDto(String msg, int statusCode, Long userId) {
        this.msg = msg;
        this.statusCode = statusCode;
        this.userId = userId;
    }
    public ResponseDto(String msg, int statusCode, Long userId, boolean profile) {
        this.msg = msg;
        this.statusCode = statusCode;
        this.userId = userId;
        this.profile = profile;
    }
}
