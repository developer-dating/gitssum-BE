package com.backend.gitssum.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomException extends RuntimeException{

    private String errorMessage;
    private int statusCode;

    public CustomException(ErrorCode errorCode) {
        this.errorMessage=errorCode.getErrorMessage();
        this.statusCode=errorCode.getStatusCode();
    }
}