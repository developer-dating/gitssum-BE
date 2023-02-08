package com.backend.gitssum.service;

import com.backend.gitssum.dto.HomeProfileResponseDto;
import com.backend.gitssum.dto.HomeUserListResponseDto;
import com.backend.gitssum.dto.RecommendationRequestDto;
import com.backend.gitssum.dto.ResponseDto;
import com.backend.gitssum.entity.ImageFile;
import com.backend.gitssum.entity.Recommendation;
import com.backend.gitssum.entity.User;
import com.backend.gitssum.repository.RecommendationRepository;
import com.backend.gitssum.repository.UserRepository;
import com.backend.gitssum.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class RecommendationService {

    private final RecommendationRepository recommendationRepository;
    private final UserRepository userRepository;

    @Transactional
    public ResponseDto saveRecommendation(RecommendationRequestDto recommendationRequestDto, UserDetailsImpl userDetails) {
        Long userId = userDetails.getUser().getId();
        User user = userRepository.findById(userId).orElse(null);
        if(recommendationRepository.existsByUserId(userId)==true){
            recommendationRepository.deleteByUserId(userId);
        }
        Recommendation recommendation = recommendationRepository.saveAndFlush(new Recommendation(recommendationRequestDto, user));
        return new ResponseDto("추천 취향 등록 완료", HttpStatus.OK.value());
    }

    @Transactional
    public ResponseDto updateRecommendation(RecommendationRequestDto recommendationRequestDto, UserDetailsImpl userDetails) {
        Long userId = userDetails.getUser().getId();
        User user = userRepository.findById(userId).orElse(null);
        Recommendation recommendation = recommendationRepository.findByUserId(userId).orElse(null);
        recommendation.update(recommendationRequestDto);
        return new ResponseDto("추천 취향 수정 완료", HttpStatus.OK.value());
    }

    //    public HomeUserListResponseDto getRecommendation(UserDetailsImpl userDetails) {
//        HomeUserListResponseDto homeUserListResponseDto = new HomeUserListResponseDto();
//        Long userId = userDetails.getUser().getId();
//        User user = userRepository.findById(userId).orElse(null);
//        String residence = user.getRecommendation().getResidence();
//        String age = user.getRecommendation().getAge();
//        List<User> users = userRepository.findAllByOrderByCreatedAtDesc();
//        //나를 제외한 유저
//        List<User> Users = new ArrayList<>();
//        for(User u: users){
//            if(u.getId() != userId ){
//                Users.add(u);
//            }
//        }
//        //나이....
//        //거주지
//        List<User> Users1 = new ArrayList<>();
//        for(User u: Users){
//            if(u.getResidence() == residence){
//                Users1.add(u);
//            }
//        }
//        //추천취향stackList
//        List<User> finalUsers = new ArrayList<>();
//        List<String> stacks = new ArrayList<>();
//        List<String> stackList = new ArrayList<>();
//        stacks.add(user.getRecommendation().getStack1());
//        stacks.add(user.getRecommendation().getStack2());
//        stacks.add(user.getRecommendation().getStack3());
//        for(String stack : stacks){
//            if(stack != null)stackList.add(stack);
//        }
//        for(User u: Users1){
//
//            List<String> stack = new ArrayList<>();
//            List<String> arrangedStackList = new ArrayList<>();
//            stack.add(u.getStack1());
//            stack.add(u.getStack2());
//            stack.add(u.getStack3());
//            for(String s : stacks){
//                if(s != null)arrangedStackList.add(s);
//            }
//            boolean result = true;
//            for(String a: stackList){
//                if(!arrangedStackList.contains(a)){
//                    result = false;
//                    break;
//                }
//            }
//            if(result == true){
//               finalUsers.add(u);
//            }
//        }
//        for(User v : finalUsers){
//                //이미지
//                List<String> imageFileList = new ArrayList<>();
//                for(ImageFile imageFile : v.getImageFileList()){
//                    imageFileList.add(imageFile.getPath());
//                }
//                List<String> finalStacks = new ArrayList<>();
//                List<String> finalStackList = new ArrayList<>();
//                finalStacks.add(user.getStack1());
//                finalStacks.add(user.getStack2());
//                finalStacks.add(user.getStack3());
//                for(String stack : finalStacks){
//                    if(stack != null)finalStackList.add(stack);
//                }
//                homeUserListResponseDto.addProfile(new HomeProfileResponseDto(user,imageFileList,stackList));
//
//        }
//        return homeUserListResponseDto;
//
//
//    }
    public HomeUserListResponseDto getRecommendation(UserDetailsImpl userDetails) {
        HomeUserListResponseDto homeUserListResponseDto = new HomeUserListResponseDto();
        if(!recommendationRepository.existsByUserId(userDetails.getUser().getId())){
            return homeUserListResponseDto;
        }
        Long userId = userDetails.getUser().getId();
        User user = userRepository.findById(userId).orElse(null);
        //지역
        String age = user.getRecommendation().getAge();
        List<User> users1 = new ArrayList<>();
        switch (age) {
            case "20-24세":
                users1 = userRepository.findallusers20to24();
                System.out.println("users1 :" + users1);
                break;
            case "25-29세":
                users1 = userRepository.findallusers25to29();
                System.out.println(users1);
                break;
            case "30-34세":
                users1 = userRepository.findallusers30to34();
                System.out.println(users1);
                break;
            case "35-39세":
                users1 = userRepository.findallusers35to39();
                System.out.println(users1);
                break;
        }
        //나를 제외한 유저
        List<User> users2 = new ArrayList<>();
        for (User u : users1) {
            if (u.getId() != userId) {
                users2.add(u);
            }
        }
        System.out.println(users2);
            //지역
            String residence = user.getRecommendation().getResidence();
            List<User> users3 = new ArrayList<>();
            for (User a : users2) {
                if (a.getResidence().equals(residence)) {
                    users3.add(a);
                }
            }
        System.out.println("users3 : " +users3);
            //스택
            //취향스택리스트불러오기
            List<User> finalUsers = new ArrayList<>();
            List<String> recommendationStacks = new ArrayList<>();
            List<String> recommendationStackList = new ArrayList<>();
            recommendationStacks.add(user.getRecommendation().getStack1());
            recommendationStacks.add(user.getRecommendation().getStack2());
            recommendationStacks.add(user.getRecommendation().getStack3());
            for (String str : recommendationStacks) {
                if (str != null) {
                    recommendationStackList.add(str);
                }
            }
        System.out.println(recommendationStackList);
            //스택비교하기
            for (User b : users3) {
                //해당유저의스택리스트
                List<String> Stacks = new ArrayList<>();
                List<String> StackList = new ArrayList<>();
                Stacks.add(b.getStack1());
                Stacks.add(b.getStack2());
                Stacks.add(b.getStack3());
                for (String str2 :Stacks ) {
                    if (str2 != null) {
                        StackList.add(str2);
                    }
                }
                System.out.println(StackList);
                boolean result = true;
                for (String str3 : recommendationStackList) {
                    if (!StackList.contains(str3)) {
                        result = false;
                        break;
                    }
                }
                if (result) {
                    finalUsers.add(b);
                }
            }
            for (User c : finalUsers) {
                List<String> imageFileList = new ArrayList<>();
                for (ImageFile imageFile : c.getImageFileList()) {
                    imageFileList.add(imageFile.getPath());
                }
                List<String> finalStacks = new ArrayList<>();
                List<String> finalStackList = new ArrayList<>();
                finalStacks.add(c.getStack1());
                finalStacks.add(c.getStack2());
                finalStacks.add(c.getStack3());
                for (String stack : finalStacks) {
                    if (stack != null) finalStackList.add(stack);
                }
                homeUserListResponseDto.addProfile(new HomeProfileResponseDto(c, imageFileList, finalStackList));
            }
        System.out.println(finalUsers);

            return  homeUserListResponseDto;
        }


    }

