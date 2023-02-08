package com.backend.gitssum.dto;


import lombok.Getter;

@Getter
public class GithubResDto {

    private String access_token;
    private String token_type;
    private String scope;
}