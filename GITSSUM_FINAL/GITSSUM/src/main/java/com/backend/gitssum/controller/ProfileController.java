package com.backend.gitssum.controller;


import com.backend.gitssum.dto.ProfileRequestDto;
import com.backend.gitssum.dto.ResponseDto;
import com.backend.gitssum.security.UserDetailsImpl;
import com.backend.gitssum.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class ProfileController {
    private final ProfileService profileService;
    //home_view 여러 프로필 조회
    @GetMapping("/get/profiles")
    public  ResponseEntity<?> getProfiles(@AuthenticationPrincipal UserDetailsImpl userDetails){
        return ResponseEntity.ok(profileService.getProfiles(userDetails.getUser()));
    }
    //home_view 프로필 상세조회
    @GetMapping("/get/profile/{id}")
    public ResponseEntity<?> getProfile(@PathVariable Long id,@AuthenticationPrincipal UserDetailsImpl userDetails){
        return ResponseEntity.ok(profileService.getUserProfile(id, userDetails.getUser()));
    }

    //프로필조회
    @GetMapping("/get/mypage")
    public ResponseEntity<?> getProfile(@AuthenticationPrincipal UserDetailsImpl userDetails){
        return ResponseEntity.ok(profileService.getProfile(userDetails));
    }

    //프로필수정
    @PutMapping("/modify/mypage")
    public ResponseEntity<?> updateProfile(@ModelAttribute ProfileRequestDto profileRequestDto,@AuthenticationPrincipal UserDetailsImpl userDetails) throws IOException {
        return ResponseEntity.ok(profileService.updateProfile(profileRequestDto,userDetails));
    }




//    @PutMapping("/modify/{userId}/mypage")
//    public ResponseEntity<?> updateProfile(@ModelAttribute ProfileRequestDto profileRequestDto,@PathVariable Long userId) throws IOException {
//        return ResponseEntity.ok(profileService.modifyProfile(profileRequestDto,userId));
//    }


}
