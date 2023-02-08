package com.backend.gitssum.service;

import com.backend.gitssum.dto.LikedMeListResponseDto;
import com.backend.gitssum.dto.ProfileResponseDto;
import com.backend.gitssum.dto.ResponseDto;
import com.backend.gitssum.entity.ImageFile;
import com.backend.gitssum.entity.LikeUser;
import com.backend.gitssum.entity.User;
import com.backend.gitssum.exception.CustomException;
import com.backend.gitssum.exception.ErrorCode;
import com.backend.gitssum.repository.LikeRepository;
import com.backend.gitssum.repository.UserRepository;
import com.backend.gitssum.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LikeService {
    private final LikeRepository likeRepository;
    private final UserRepository userRepository;

    @Transactional
    public ResponseDto likeUser(Long userId, User user) {
        User user1 = userRepository.findById(userId).orElseThrow(
                () -> new CustomException(ErrorCode.USER_NOT_FOUND)
        );
        if(likeRepository.existsByLikedUserIdAndLikerUserId(user.getId(),userId)==true){
            likeRepository.deleteByLikedUserIdAndLikerUserId(user.getId(),userId);
            return new ResponseDto("매칭 성공", HttpStatus.OK.value());
        }
        else {
            LikeUser likesUser = new LikeUser(user.getId(), userId);
            likeRepository.save(likesUser);
            return new ResponseDto("좋아요 성공", HttpStatus.OK.value());
        }
    }
    @Transactional(readOnly = true)
    public LikedMeListResponseDto likedMe(UserDetailsImpl userDetails) {
        LikedMeListResponseDto likedMeListResponseDto = new LikedMeListResponseDto();
        List<LikeUser> likedUsers = likeRepository.findAll();
        System.out.println("유저아이디"+userDetails.getUser());
//        List<String> imageFileList = new ArrayList<>();
//        List<String> stacks = new ArrayList<>();
//        List<String> stackList = new ArrayList<>();
        for(LikeUser likeUser : likedUsers){
            if(likeUser.getLikedUserId()==userDetails.getUser().getId()){
                User user1 = userRepository.findById(likeUser.getLikerUserId()).orElseThrow(
                        () -> new CustomException(ErrorCode.USER_NOT_FOUND)
                );
                List<String> imageFileList = new ArrayList<>();
                for(ImageFile imageFile : user1.getImageFileList()){
                    imageFileList.add(imageFile.getPath());
                }
                List<String> stacks = new ArrayList<>();
                List<String> stackList = new ArrayList<>();
                stacks.add(user1.getStack1());
                stacks.add(user1.getStack2());
                stacks.add(user1.getStack3());
                for(String stack : stacks){
                    if(stack != null)stackList.add(stack);
                }
                likedMeListResponseDto.addLikeFeed(new ProfileResponseDto(user1,imageFileList,stackList));
//                stacks.clear();
//                stackList.clear();
//                imageFileList.clear();
            }
        }
        return likedMeListResponseDto;
//        Long UserId = userDetails.getUser().getId();
//        List<LikeUser> likedMeUsers = likeRepository.findAllBylikedUserId(UserId);
//        System.out.print("나를좋아요한유저" + likedMeUsers);
////        for(User user1: likedMeUsers){
////            User likedMeUser =
////        }
//        return new ResponseDto("날 좋아요한사람들 가져오기 성공", HttpStatus.OK.value());
    }

}