package com.backend.gitssum.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    //400 ERROR

    LAST_MESSAGE_NOT_FOUND(BAD_REQUEST.value(),"마지막 메시지를 찾을 수 없습니다."),
    NOT_VALID_CONTENT(BAD_REQUEST.value(),"유효하지 않는 내용입니다"),
    USER_NOT_FOUND(BAD_REQUEST.value(), "회원을 찾을 수 없습니다."),
    OVERLAP_EMAIL(BAD_REQUEST.value(), "중복된 email 주소 입니다."),
    OVERLAP_USERNAME(BAD_REQUEST.value(), "중복된 username 입니다."),
    MISMATCH_PASSWORD(BAD_REQUEST.value(), "비밀번호가 일치하지 않습니다."),
    MISMATCH_ADMIN_TOKEN(BAD_REQUEST.value(), "관리자 토큰값이 일치하지 않습니다."),
    INVALID_TOKEN(BAD_REQUEST.value(), "토큰이 유효하지 않습니다."),
    EXPIRED_TOKEN(BAD_REQUEST.value(), "만료된 토큰입니다."),
    UNSUPPORTED_TOKEN(BAD_REQUEST.value(), "지원되지 않는 토큰입니다."),
    NOT_ALLOW_UPDATE(BAD_REQUEST.value(), "작성자만 수정할 수 있습니다."),
    NOT_ALLOW_DELETE(BAD_REQUEST.value(), "작성자만 삭제할 수 있습니다."),
    NOT_ALLOW_WITHDRAWAL(BAD_REQUEST.value(), "회원탈퇴를 실패했습니다."),
    POST_NOT_FOUND(BAD_REQUEST.value(), "게시글을 찾을 수 없습니다"),
    COMMENT_NOT_FOUND(BAD_REQUEST.value(), "댓글을 찾을 수 없습니다"),

    //Room
    NOT_EXIST_ROOM(BAD_REQUEST.value(),"채팅방이 존재하지 않습니다.");

    private final int statusCode;
    private final String errorMessage;

}