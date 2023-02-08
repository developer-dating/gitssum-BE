package com.backend.gitssum.dto;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ProfileRequestDto {
    //private MultipartFile file;
    private String username;
    private int age;
    private String gender;
    private List<MultipartFile> multipartFile;
    private String introduction;
    private String link;
    private String education;
    private String job;
    private String residence;
    private List<String> stacks;
//    private String stack1;
//    private String stack2;
//    private String stack3;


}
