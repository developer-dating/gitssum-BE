package com.backend.gitssum.dto;


import com.backend.gitssum.entity.User;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ProfileResponseDto {

    private Long userId;
    private String username;
    private Integer age;
    private String gender;
    private List<String> imageList;

    private String introduction;
    private String link;
    private String education;
    private String job;
    private String residence;


    private List<String> stackList;

    public ProfileResponseDto(User user,List<String> imageFileList){
        this.userId = user.getId();
        this.username = user.getUsername();
        this.age = user.getAge();
        this.gender = user.getGender();
        this.imageList = imageFileList;
        this.link = user.getGithublink();
        this.education = user.getEducation();
        this.job = user.getJob();
        this.residence = user.getResidence();

    }
    public ProfileResponseDto(User user,List<String> imageFileList, List<String> stackList){
        this.userId = user.getId();
        this.username = user.getUsername();
        this.age = user.getAge();
        this.gender = user.getGender();
        this.imageList = imageFileList;
        this.link = user.getGithublink();
        this.introduction = user.getIntroduction();
        this.education = user.getEducation();
        this.job = user.getJob();
        this.residence = user.getResidence();
        this.stackList = stackList;

    }


}
