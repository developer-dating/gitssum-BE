package com.backend.gitssum.entity;

import com.backend.gitssum.dto.ProfileRequestDto;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "USERS")
public class User extends TimeStamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USER_ID")
    private Long id;
    private Long kakaoId;
    private String googleId;
    @Column//(nullable = false)
    private String email;

    @Column//(nullable = false)
    private String password;
    @Column//(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private UserRoleEnum role;
    @Column//(nullable = false)
    private String username;

    @Column//(nullable = false)
    private Integer age;

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, fetch = FetchType.EAGER)
    private List<ImageFile> imageFileList = new ArrayList<>();

    @Column//(nullable = false)
    private String introduction;

    @Column//(nullable = false)
    private String githublink;

    @Column//(nullable = false)
    private String gender;

    @Column//(nullable = false)
    private String job;


    @Column//(nullable = false)
    private String education;

    @Column//(nullable = false)
    private String residence;

    @Column//(nullable = false)
    private String stack1;

    @Column
    private String stack2;

    @Column
    private String stack3;

    @OneToOne(mappedBy = "user", cascade = CascadeType.REMOVE)
    private Recommendation recommendation;




    public User(Long kakaoId, String encodedpassword, String email, UserRoleEnum role){
        this.password = encodedpassword;
        this.kakaoId = kakaoId;
        this.role = role;
        this.email = email;
    }
    public User(String googleId, String encodedpassword, String email, UserRoleEnum role){
        this.password = encodedpassword;
        this.googleId = googleId;
        this.role = role;
        this.email = email;
    }
    public User kakaoIdUpdate(Long kakaoId){
        this.kakaoId = kakaoId;
        return this;
    }
    public User(ProfileRequestDto profileRequestDto){
        this.username = profileRequestDto.getUsername();
        this.age = profileRequestDto.getAge();
        this.gender = profileRequestDto.getGender();
        this.introduction = profileRequestDto.getIntroduction();
        this.githublink = profileRequestDto.getLink();
        this.education = profileRequestDto.getEducation();
        this.job = profileRequestDto.getJob();
        this.residence = profileRequestDto.getResidence();
        List<String> stackList = new ArrayList<>();
        stackList = profileRequestDto.getStacks();
        if(stackList.size()==1){
            this.stack1 = stackList.get(0);
            this.stack2 = null;
            this.stack3 = null;
        } else if (stackList.size() == 2) {
            this.stack1 = stackList.get(0);
            this.stack2 = stackList.get(1);
            this.stack3 = null;
        }
        else{
            this.stack1 = stackList.get(0);
            this.stack2 = stackList.get(1);
            this.stack3 = stackList.get(2);
        }
    }
    public void update(ProfileRequestDto profileRequestDto){
        this.username = profileRequestDto.getUsername();
        this.age = profileRequestDto.getAge();
        this.gender = profileRequestDto.getGender();
        this.introduction = profileRequestDto.getIntroduction();
        this.githublink = profileRequestDto.getLink();
        this.education = profileRequestDto.getEducation();
        this.job = profileRequestDto.getJob();
        this.residence = profileRequestDto.getResidence();
        List<String> stackList = new ArrayList<>();
        stackList = profileRequestDto.getStacks();
        if(stackList.size()==1){
            this.stack1 = stackList.get(0);
            this.stack2 = null;
            this.stack3 = null;
        } else if (stackList.size() == 2) {
            this.stack1 = stackList.get(0);
            this.stack2 = stackList.get(1);
            this.stack3 = null;
        }
        else{
            this.stack1 = stackList.get(0);
            this.stack2 = stackList.get(1);
            this.stack3 = stackList.get(2);
        }
    }
}
