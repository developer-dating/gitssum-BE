package com.backend.gitssum.controller;

import com.backend.gitssum.dto.ResponseDto;
import com.backend.gitssum.jwt.JwtUtil;
import com.backend.gitssum.service.GithubService;
import com.backend.gitssum.service.GoogleService;
import com.backend.gitssum.service.KakaoService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@RequiredArgsConstructor
@RequestMapping("/login")
@RestController
public class UserController {

    private final KakaoService kakaoService;
    private final GoogleService googleService;
    private final GithubService githubService;


    @GetMapping("/oauth2/kakao1")
    public ResponseDto kakaoLogin(@RequestParam String code, HttpServletResponse response) throws JsonProcessingException {
        // code: 카카오 서버로부터 받은 인가 코드
        return kakaoService.kakaoLogin(code, response);

//         Cookie 생성 및 직접 브라우저에 Set
//        Cookie cookie = new Cookie(JwtUtil.AUTHORIZATION_HEADER, createToken.substring(7));
//        cookie.setPath("/");
//        response.addCookie(cookie);
    }
        @GetMapping("/oauth2/google")
    public ResponseDto googleLogin(@RequestParam(value = "code") String code, HttpServletResponse response) throws JsonProcessingException {
        return googleService.googleLogin(code, response);
    }
        @GetMapping("/oauth2/github")
    public ResponseDto githubLogin(@RequestParam(value = "code") String code, HttpServletResponse response) throws JsonProcessingException {
        return githubService.githubLogin(code, response);
    }
}
