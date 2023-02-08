package com.backend.gitssum.controller;

import com.backend.gitssum.dto.LikedMeListResponseDto;
import com.backend.gitssum.dto.ResponseDto;
import com.backend.gitssum.security.UserDetailsImpl;
import com.backend.gitssum.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/like")
public class LikeController {
    private final LikeService likeService;

    //좋아요 하기
    @PostMapping("/user/{userId}")
    public ResponseDto likePost(@PathVariable Long userId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return likeService.likeUser(userId, userDetails.getUser());
    }
    //날좋아요한사람리스트
    @GetMapping("/get/likeme")
    public LikedMeListResponseDto likedMe(@AuthenticationPrincipal UserDetailsImpl userDetails){
        return likeService.likedMe(userDetails);
    }

}
