package com.backend.gitssum.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class RecommendationRequestDto {

    private String residence;
    private String age;
//    private String stack1;
//    private String stack2;
//    private String stack3;
    private List<String> stacks;
}
