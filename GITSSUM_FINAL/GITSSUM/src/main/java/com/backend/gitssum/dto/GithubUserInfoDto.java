package com.backend.gitssum.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class GithubUserInfoDto {
    private Long id;
    private String nickname;
    private String email;
    private String userImgUrl;
}