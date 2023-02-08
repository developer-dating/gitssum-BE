package com.backend.gitssum.dto;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class LikedMeListResponseDto {

    List<ProfileResponseDto> likeFeed = new ArrayList<>();

    public void addLikeFeed(ProfileResponseDto responseDto) {
        likeFeed.add(responseDto);
    }

}