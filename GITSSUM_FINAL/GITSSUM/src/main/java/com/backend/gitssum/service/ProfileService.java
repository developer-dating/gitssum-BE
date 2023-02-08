package com.backend.gitssum.service;


import com.backend.gitssum.dto.*;
import com.backend.gitssum.entity.ImageFile;
import com.backend.gitssum.entity.User;
import com.backend.gitssum.exception.CustomException;
import com.backend.gitssum.exception.ErrorCode;
import com.backend.gitssum.repository.ImageFileRepository;
import com.backend.gitssum.repository.LikeRepository;
import com.backend.gitssum.repository.UserRepository;
import com.backend.gitssum.s3.AmazonS3Service;
import com.backend.gitssum.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.LocalDate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProfileService {
    private final UserRepository userRepository;
    private final ImageFileRepository imageFileRepository;
    private final AmazonS3Service s3Service;
    private final LikeRepository likeRepository;

    @Transactional(readOnly = true)
    public HomeUserListResponseDto getProfiles(User user) {
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
        LocalDateTime now11 = LocalDateTime.now();
        Long userId = user.getId();
        System.out.println("userid는"+user.getId());
        System.out.println(now);
        System.out.println(now11);
        HomeUserListResponseDto homeUserListResponseDto = new HomeUserListResponseDto();
        List<User> users = userRepository.findAllByOrderByModifiedAtDesc();
        List<User> Users = new ArrayList<>();
        for(User user1: users){
            Users.add(user1);
            if(user1.getId() == userId ){
                Users.remove(user1);
            }
        }
        for(User user1: Users){
           //이미지
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
            boolean liked;
            if(likeRepository.existsByLikedUserIdAndLikerUserId(user1.getId(),userId)==true){
                liked = true;
            }
            else{
                liked = false;
            }
            homeUserListResponseDto.addProfile(new HomeProfileResponseDto(user1,imageFileList,stackList, liked));
        }
        return homeUserListResponseDto;
    }

    @Transactional(readOnly = true)
    public ProfileResponseDto getProfile(UserDetailsImpl userDetails){
        Long userId = userDetails.getUser().getId();
        User user = userRepository.findById(userId).orElse(null);
        List<String> imageFileList = new ArrayList<>();
        for(ImageFile imageFile : user.getImageFileList()){
            imageFileList.add(imageFile.getPath());
        }
        List<String> stacks = new ArrayList<>();
        List<String> stackList = new ArrayList<>();
        stacks.add(user.getStack1());
        stacks.add(user.getStack2());
        stacks.add(user.getStack3());
        for(String stack : stacks){
            if(stack != null)stackList.add(stack);
        }
        return new ProfileResponseDto(user,imageFileList,stackList);
    }

    @Transactional
    public ResponseDto updateProfile(ProfileRequestDto profileRequestDto, UserDetailsImpl userDetails) throws IOException {
        Long userId = userDetails.getUser().getId();
        User user = userRepository.findById(userId).orElse(null);
        user.update(profileRequestDto);
        if(!profileRequestDto.getMultipartFile().isEmpty()){
            List<ImageFile> imageFileList = imageFileRepository.findAllByUser(user);
            for(ImageFile imageFile: imageFileList){
                String path = imageFile.getPath();
                String filename = path.substring(56);
                s3Service.deleteFile(filename);
            }
            imageFileRepository.deleteAll(imageFileList);
            s3Service.upload(profileRequestDto.getMultipartFile(),"profile",user);
        }
        return new ResponseDto("프로필 수정 완료", 200);
    }

    @Transactional(readOnly = true)
    public HomeProfileResponseDto getUserProfile(Long id, User user) {
        User user1 = userRepository.findById(id).orElseThrow(
                () -> new CustomException(ErrorCode.USER_NOT_FOUND)
        );
        List<String> imageFileList = new ArrayList<>();
        for(ImageFile imageFile : user1.getImageFileList()){
            imageFileList.add(imageFile.getPath());
        }
        System.out.println("이미지이미지이미지:"+imageFileList);
        List<String> stacks = new ArrayList<>();
        List<String> stackList = new ArrayList<>();
        stacks.add(user1.getStack1());
        stacks.add(user1.getStack2());
        stacks.add(user1.getStack3());
        for(String stack : stacks){
            if(stack != null)stackList.add(stack);
        }
        return new HomeProfileResponseDto(user1,imageFileList,stackList);
        }



//    public ResponseDto modifyProfile(ProfileRequestDto profileRequestDto, Long userId) throws IOException {
//        User user = userRepository.findById(userId).orElseThrow();
//        user.update(profileRequestDto);
//        if(!profileRequestDto.getMultipartFile().isEmpty()){
//            List<ImageFile> imageFileList = imageFileRepository.findAllByUser(user);
//            for(ImageFile imageFile: imageFileList){
//                String path = imageFile.getPath();
//                String filename = path.substring(56);
//                s3Service.deleteFile(filename);
//            }
//            imageFileRepository.deleteAll(imageFileList);
//            s3Service.upload(profileRequestDto.getMultipartFile(),"profile",user);
//        }
//        return new ResponseDto("프로필 수정 완료", 200);
//    }


}
