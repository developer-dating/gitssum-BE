package com.backend.gitssum.dto;

import com.backend.gitssum.entity.User;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class HomeProfileResponseDto {
    private Long userId;
    private List<String> imageList;
    private String username;
    private Integer age;
    private String job;
    private String residence;
    private String introduction;
    private String education;
    private String link;
    private List<String> stackList;
    private boolean liked;

    public HomeProfileResponseDto(User user,List<String> imageFileList, List<String> stackList){
        this.userId = user.getId();
        this.imageList = imageFileList;
        this.username = user.getUsername();
        this.age = user.getAge();
        this.job = user.getJob();
        this.residence = user.getResidence();
        this.introduction = user.getIntroduction();
        this.education = user.getEducation();
        this.link = user.getGithublink();
        this.stackList = stackList;
    }
    public HomeProfileResponseDto(User user,List<String> imageFileList, List<String> stackList, boolean liked){
        this.userId = user.getId();
        this.imageList = imageFileList;
        this.username = user.getUsername();
        this.age = user.getAge();
        this.job = user.getJob();
        this.residence = user.getResidence();
        this.introduction = user.getIntroduction();
        this.education = user.getEducation();
        this.link = user.getGithublink();
        this.stackList = stackList;
        this.liked = liked;
    }

}
