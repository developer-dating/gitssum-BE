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
        // 1. "????????????" ??? "????????? ??????" ??????
        String getAccessToken = getAccessToken(code);

        // 2. ???????????? ?????? API ??????
        GoogleUserInfoDto googleUserInfo = getGoogleUserInfo(getAccessToken);

        // 3.  ??????ID??? ???????????? ??????
        User googleUser = signupGoogleUser(googleUserInfo);

        //4. ?????? ????????? ??????
        forceLoginUser(googleUser);

//        //?????? ??????
//        TokenDto tokenDto = jwtUtil.createAllToken(member.getEmail());
//
//        Optional<RefreshToken> refreshToken = refreshTokenRepository.findByAccountEmail(member.getEmail());
//
//        // ??????????????? ??? ???????????? ?????? ??????????
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
        //????????? header??? ????????? ????????????????????? ????????????
        response.addHeader(JwtUtil.AUTHORIZATION_HEADER, jwtUtil.createToken(googleUser.getEmail(),googleUser.getRole()));
//        return createToken;
        return new ResponseDto("???????????? ??????", HttpStatus.OK.value(),googleUser.getId(), profile);
    }

    private void forceLoginUser(User user) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        Authentication createAuthentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(createAuthentication);
    }

    //header ??? Content-type ??????
    public String getAccessToken(String code) throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        //HTTP Body ??????
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", client_id);
        body.add("client_secret", clientSecret);
        body.add("redirect_uri", redirect_uri);
        body.add("code", code);
//AIzaSyDJeN3R739uAmrVSr7l0xcRK666M26ybYo

        //HTTP ?????? ?????????
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

        //HTTP ?????? (JSON) -> ????????? ?????? ??????
        //JSON -> JsonNode ????????? ??????
        String responseBody = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        return jsonNode.get("access_token").asText();
    }

    public GoogleUserInfoDto getGoogleUserInfo(String accessToken) throws JsonProcessingException {
        // HTTP Header ??????
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
        // HTTP ?????? ?????????
        HttpEntity<MultiValueMap<String, String>> kakaoUserInfoRequest = new HttpEntity<>(headers);
        RestTemplate rt = new RestTemplate();
        rt.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
        ResponseEntity<String> response = rt.exchange(
                "https://openidconnect.googleapis.com/v1/userinfo",
                HttpMethod.POST,
                kakaoUserInfoRequest,
                String.class
        );
        //HTTP ?????? (JSON)
        //JSON -> JsonNode ????????? ??????
        String responseBody = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        String id = jsonNode.get("sub").asText();

        String userEmail = jsonNode.get("email").asText();

        String userName = jsonNode.get("name").asText();
        return new GoogleUserInfoDto(id, userName, userEmail);
    }

    private User signupGoogleUser(GoogleUserInfoDto googleUserInfoDto) {
        // ????????? ??????
        // DB ??? ????????? Google Id ??? ????????? ??????
        String googleId = googleUserInfoDto.getGoogleId();
        User findGoogle = userRepository.findByEmail("g_" + googleUserInfoDto.getUserEmail()).orElse(null);


        //DB??? ????????? ????????? ????????? ???????????? ??????
        if (findGoogle == null) {

            String email = ("g_"+googleUserInfoDto.getUserEmail());
            String password = UUID.randomUUID().toString();
            String encodedPassword = passwordEncoder.encode(password);
//
//            User googleMember = User.builder()
//                    .email("g_" + email)
//                    .userName(comfortUtils.makeUserNickName())
//                    .userImgUrl("https://eunibucket.s3.ap-northeast-2.amazonaws.com/testdir/normal_user_img.png")//????????????
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
