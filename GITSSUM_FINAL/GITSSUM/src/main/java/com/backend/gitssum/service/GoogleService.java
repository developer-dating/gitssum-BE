package com.backend.gitssum.service;


import com.backend.gitssum.dto.GoogleUserInfoDto;
import com.backend.gitssum.dto.ResponseDto;
import com.backend.gitssum.entity.User;
import com.backend.gitssum.entity.UserRoleEnum;
import com.backend.gitssum.jwt.JwtUtil;
import com.backend.gitssum.repository.UserRepository;
import com.backend.gitssum.security.UserDetailsServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.oauth2.sdk.token.RefreshToken;
import com.nimbusds.openid.connect.sdk.claims.UserInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.h2.engine.Role;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class GoogleService {

    @Value("https://main.d20iwpsyv6d6f7.amplifyapp.com/login/oauth2/google")
    String redirect_uri;
    @Value("1002157101396-1c36k7hdfahq9vt2njpeq9cjmd5eo7h9.apps.googleusercontent.com")
    String client_id;
    @Value("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")
    String clientSecret;

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserDetailsServiceImpl userDetailsService;

    public ResponseDto googleLogin(String code, HttpServletResponse response)
            throws JsonProcessingException {
        // 1. "인가코드" 로 "액세스 토큰" 요청
        String getAccessToken = getAccessToken(code);

        // 2. 토큰으로 구글 API 호출
        GoogleUserInfoDto googleUserInfo = getGoogleUserInfo(getAccessToken);

        // 3.  구글ID로 회원가입 처리
        User googleUser = signupGoogleUser(googleUserInfo);

        //4. 강제 로그인 처리
        forceLoginUser(googleUser);

//        //토큰 발급
//        TokenDto tokenDto = jwtUtil.createAllToken(member.getEmail());
//
//        Optional<RefreshToken> refreshToken = refreshTokenRepository.findByAccountEmail(member.getEmail());
//
//        // 로그아웃한 후 로그인을 다시 하는가?
//        if(refreshToken.isPresent()) {
//            RefreshToken refreshToken1 = refreshToken.get().updateToken(tokenDto.getRefreshToken());
//            refreshTokenRepository.save(refreshToken1);
//        } else {
//            RefreshToken newToken = new RefreshToken(tokenDto.getRefreshToken(), member.getEmail());
//            refreshTokenRepository.save(newToken);
//        }
        boolean profile;
        if(googleUser.getAge()==null){
            profile = false;
        }
        else{
            profile = true;
        }
        //토큰을 header에 넣어서 클라이언트에게 전달하기
        response.addHeader(JwtUtil.AUTHORIZATION_HEADER, jwtUtil.createToken(googleUser.getEmail(),googleUser.getRole()));
//        return createToken;
        return new ResponseDto("회원가입 성공", HttpStatus.OK.value(),googleUser.getId(), profile);
    }

    private void forceLoginUser(User user) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        Authentication createAuthentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(createAuthentication);
    }

    //header 에 Content-type 지정
    public String getAccessToken(String code) throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        //HTTP Body 생성
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", client_id);
        body.add("client_secret", clientSecret);
        body.add("redirect_uri", redirect_uri);
        body.add("code", code);
//AIzaSyDJeN3R739uAmrVSr7l0xcRK666M26ybYo

        //HTTP 요청 보내기
        HttpEntity<MultiValueMap<String, String>> googleTokenRequest =
                new HttpEntity<>(body, headers);
        RestTemplate rt = new RestTemplate();
        rt.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
        ResponseEntity<String> response = rt.exchange(
                "https://oauth2.googleapis.com/token",
                HttpMethod.POST,
                googleTokenRequest,
                String.class
        );

        //HTTP 응답 (JSON) -> 액세스 토큰 파싱
        //JSON -> JsonNode 객체로 변환
        String responseBody = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        return jsonNode.get("access_token").asText();
    }

    public GoogleUserInfoDto getGoogleUserInfo(String accessToken) throws JsonProcessingException {
        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
        // HTTP 요청 보내기
        HttpEntity<MultiValueMap<String, String>> kakaoUserInfoRequest = new HttpEntity<>(headers);
        RestTemplate rt = new RestTemplate();
        rt.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
        ResponseEntity<String> response = rt.exchange(
                "https://openidconnect.googleapis.com/v1/userinfo",
                HttpMethod.POST,
                kakaoUserInfoRequest,
                String.class
        );
        //HTTP 응답 (JSON)
        //JSON -> JsonNode 객체로 변환
        String responseBody = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        String id = jsonNode.get("sub").asText();

        String userEmail = jsonNode.get("email").asText();

        String userName = jsonNode.get("name").asText();
        return new GoogleUserInfoDto(id, userName, userEmail);
    }

    private User signupGoogleUser(GoogleUserInfoDto googleUserInfoDto) {
        // 재가입 방지
        // DB 에 중복된 Google Id 가 있는지 확인
        String googleId = googleUserInfoDto.getGoogleId();
        User findGoogle = userRepository.findByEmail("g_" + googleUserInfoDto.getUserEmail()).orElse(null);


        //DB에 중복된 계정이 없으면 회원가입 처리
        if (findGoogle == null) {

            String email = ("g_"+googleUserInfoDto.getUserEmail());
            String password = UUID.randomUUID().toString();
            String encodedPassword = passwordEncoder.encode(password);
//
//            User googleMember = User.builder()
//                    .email("g_" + email)
//                    .userName(comfortUtils.makeUserNickName())
//                    .userImgUrl("https://eunibucket.s3.ap-northeast-2.amazonaws.com/testdir/normal_user_img.png")//수정필요
//                    .pw(passwordEncoder.encode(password))
//                    .isAccepted(false)
//                    .isDeleted(false)
//                    .role(Role.USER)
//                    .build();

            findGoogle = new User(googleId, encodedPassword, email,UserRoleEnum.USER);
            userRepository.save(findGoogle);

            return findGoogle;
        }

        return findGoogle;
    }
}
