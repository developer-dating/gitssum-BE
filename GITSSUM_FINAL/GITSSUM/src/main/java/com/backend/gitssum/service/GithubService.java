package com.backend.gitssum.service;

import com.backend.gitssum.dto.GithubResDto;
import com.backend.gitssum.dto.GithubUserInfoDto;
import com.backend.gitssum.dto.ResponseDto;
import com.backend.gitssum.entity.User;
import com.backend.gitssum.entity.UserRoleEnum;
import com.backend.gitssum.jwt.JwtUtil;
import com.backend.gitssum.repository.UserRepository;
import com.backend.gitssum.security.UserDetailsServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.nimbusds.oauth2.sdk.token.RefreshToken;
import lombok.RequiredArgsConstructor;
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

@RequiredArgsConstructor
@Service
public class GithubService{

    @Value("d83af89cb38b172425e9")
    private String githubClientId;
    @Value("4ef53341f33fcaf96786c463b73d0c1faf16a98c")
    private String githubCClientSecret;

    public final UserRepository userRepository;

    private final UserDetailsServiceImpl userDetailsService;
    private final PasswordEncoder passwordEncoder;
    public final JwtUtil jwtUtil;

    public ResponseDto githubLogin(String code, HttpServletResponse response) throws JsonProcessingException {
        //인가코드를 통해 accesstoken받기
        String accessToken = issuedAccessToken(code);

        //accesstoken을 이용해 사용자 정보 가져오기
        GithubUserInfoDto githubUserInfo = getGithubUserInfo(accessToken);

        //사용자 정보를 토대로 가입진행
        User githubUser = saveMember(githubUserInfo);

        //강제로그인처리
        forceLoginUser(githubUser);

//        //리프레쉬, 액세스 토큰 만든후 반환
//        createToken(member,response);
//
//        UserInfoDto userInfoDto = new UserInfoDto(member);
        response.addHeader(JwtUtil.AUTHORIZATION_HEADER, jwtUtil.createToken(githubUser.getEmail(),githubUser.getRole()));
        boolean profile;
        if(githubUser.getAge()==null){
            profile = false;
        }
        else{
            profile = true;
        }
//        return createToken;
        return new ResponseDto("회원가입 성공", HttpStatus.OK.value(), githubUser.getId(), profile);
    }
    private void forceLoginUser(User user) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        Authentication createAuthentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(createAuthentication);
    }

    private String issuedAccessToken(String code){
        HttpHeaders headers = new HttpHeaders();
        headers.add("Accept","application/json");

        // HTTP Body 생성
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("client_id", githubClientId);
//        body.add("redirect_uri","http://localhost:8080/member/signup/github");
//        body.add("redirect_uri","http://localhost:3000/login/oauth2/github");
        body.add("redirect_uri","https://main.d20iwpsyv6d6f7.amplifyapp.com/login/oauth2/github");
        body.add("code", code);
        body.add("client_secret",githubCClientSecret);

        // HTTP 요청 보내기
        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest =
                new HttpEntity<>(body,headers);
        RestTemplate rt = new RestTemplate();
        rt.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
        ResponseEntity<String> response = rt.exchange(
                "https://github.com/login/oauth/access_token",
                HttpMethod.POST,
                kakaoTokenRequest,
                String.class
        );

        // HTTP 응답 (JSON) -> 액세스 토큰 파싱
        String responseBody = response.getBody();
        Gson gson = new Gson();
        GithubResDto githubResDto = gson.fromJson(responseBody, GithubResDto.class);
        return githubResDto.getAccess_token();
    }

    private GithubUserInfoDto getGithubUserInfo(String accessToken) throws JsonProcessingException {

        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Accept","application/vnd.github+json");
        headers.add("Authorization", "Bearer " + accessToken);

        // HTTP 요청 보내기
        HttpEntity<MultiValueMap<String, String>> githubUserInfoRequest = new HttpEntity<>(headers);
        RestTemplate rt = new RestTemplate();
        rt.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
        ResponseEntity<String> response = rt.exchange(
                "https://api.github.com/user",
                HttpMethod.GET,
                githubUserInfoRequest,
                String.class
        );

        String responseBody = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseBody);

        Long id = jsonNode.get("id").asLong();
        String nickname = jsonNode.get("name").asText();
        String email = jsonNode.get("email").asText();
        String userImgUrl = jsonNode.get("avatar_url").asText();

        return new GithubUserInfoDto(id, nickname, email, userImgUrl);
    }

    public User saveMember(GithubUserInfoDto githubUserInfoDto) {
        User githubMember = userRepository.findByEmail("git_"+githubUserInfoDto.getEmail()).orElse(null);
        Long githubId = githubUserInfoDto.getId();

        //없다면 저장
        if (githubMember == null) {
            UserRoleEnum role;
            if (userRepository.findAll().isEmpty()) {
                role = UserRoleEnum.ADMIN;
            } else {
                role = UserRoleEnum.USER;
            }

            String email;
            if(githubUserInfoDto.getEmail().isBlank()){
                email = "git_"+githubUserInfoDto.getEmail();
            }else{
                do {
                    email = "git_" + UUID.randomUUID().toString().substring(0, 7) + "@git.com";
                } while (userRepository.findByEmail(email).isPresent());
            }

            String password = UUID.randomUUID().toString();
            String encodedPassword = passwordEncoder.encode(password);
//            Member member = Member.builder().
//                    email(email)
//                    .userName(socialUserInfoDto.getNickname())
//                    .userImgUrl(socialUserInfoDto.getUserImgUrl())
//                    .pw(passwordEncoder.encode(UUID.randomUUID().toString()))
//                    .isAccepted(false)
//                    .isDeleted(false)
//                    .role(role)
//                    .build();
//
//            memberRepository.save(member);
//            return member;
            githubMember = new User(githubId, encodedPassword, email, UserRoleEnum.USER);
            userRepository.save(githubMember);

            return githubMember;
        }

        //있다면 member 반환
        return githubMember;
    }

//    @Override
//    public void createToken(Member member, HttpServletResponse response){
//        TokenDto tokenDto = jwtUtil.createAllToken(member.getEmail());
//
//        Optional<RefreshToken> refreshToken = refreshTokenRepository.findByAccountEmail(member.getEmail());
//
//        if (refreshToken.isPresent()) {
//            refreshTokenRepository.save(refreshToken.get().updateToken(tokenDto.getRefreshToken()));
//        } else {
//            RefreshToken newToken = new RefreshToken(tokenDto.getRefreshToken(), member.getEmail());
//            refreshTokenRepository.save(newToken);
//        }
//
//        setHeader(response, tokenDto);
//    }
}