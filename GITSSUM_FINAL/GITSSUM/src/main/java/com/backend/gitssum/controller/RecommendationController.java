package com.backend.gitssum.controller;


import com.backend.gitssum.dto.RecommendationRequestDto;
import com.backend.gitssum.security.UserDetailsImpl;
import com.backend.gitssum.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class RecommendationController {
    private final RecommendationService recommendationService;

    //추천취향post
    @PostMapping("/save/recommendation")
    public ResponseEntity<?> saveRecommendation(@RequestBody RecommendationRequestDto recommendationRequestDto, @AuthenticationPrincipal UserDetailsImpl userDetails){
        return ResponseEntity.ok(recommendationService.saveRecommendation(recommendationRequestDto, userDetails));
    }

    //추천취향Put
    @PutMapping("/modify/recommendation")
    public ResponseEntity<?> updateRecommendation(@RequestBody RecommendationRequestDto recommendationRequestDto, @AuthenticationPrincipal UserDetailsImpl userDetails){
        return ResponseEntity.ok(recommendationService.updateRecommendation(recommendationRequestDto, userDetails));
    }

    //추천취향유저들get
    @GetMapping("/get/recommendation")
    public ResponseEntity<?> getRecommendation(@AuthenticationPrincipal UserDetailsImpl userDetails){
        return ResponseEntity.ok(recommendationService.getRecommendation(userDetails));
    }
}
