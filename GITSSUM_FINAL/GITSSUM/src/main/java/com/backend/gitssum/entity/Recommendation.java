package com.backend.gitssum.entity;


import com.backend.gitssum.dto.RecommendationRequestDto;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor
public class Recommendation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)           // image 경로
    private String residence;

    @Column
    private String age;

    @Column//(nullable = false)
    private String stack1;

    @Column
    private String stack2;

    @Column
    private String stack3;

    @OneToOne
    @JoinColumn(name = "USER_ID")
    private User user;

    public Recommendation(RecommendationRequestDto requestDto, User user){
        this.residence = requestDto.getResidence();
        this.age = requestDto.getAge();
        List<String> stackList = new ArrayList<>();
        stackList = requestDto.getStacks();
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
        this.user = user;
    }

    public void update(RecommendationRequestDto requestDto){
        this.residence = requestDto.getResidence();
        this.age = requestDto.getAge();
        List<String> stackList = new ArrayList<>();
        stackList = requestDto.getStacks();
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
