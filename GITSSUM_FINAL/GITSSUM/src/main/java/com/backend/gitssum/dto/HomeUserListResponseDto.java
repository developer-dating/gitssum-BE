package com.backend.gitssum.dto;


import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class HomeUserListResponseDto {
    List<HomeProfileResponseDto> profileList = new ArrayList<>();

    public void addProfile(HomeProfileResponseDto responseDto){
        profileList.add(responseDto);
    }

}
