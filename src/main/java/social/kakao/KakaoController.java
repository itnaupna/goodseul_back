package social.kakao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import data.entity.UserEntity;
import data.repository.UserRepository;
import jwt.setting.config.Role;
import jwt.setting.config.SocialType;
import jwt.setting.settings.JwtService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.core.userdetails.User;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

@Slf4j
@CrossOrigin
@RestController
public class KakaoController {
    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String client_id;
    @Value("${spring.security.oauth2.client.registration.kakao.client-secret}")
    private String client_secret;
    @Value("${spring.security.oauth2.client.provider.kakao.authorization-uri}")
    private String a_uri;
    @Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
    private String redirect_uri;

    private final UserRepository userRepository;
    private final JwtService jwtService;

    public KakaoController(UserRepository userRepository, JwtService jwtService) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }
    @PostMapping("/api/lv0/kakao")
    private ResponseEntity<?> kakaoCallBack(@RequestBody JsonNode json) throws JsonProcessingException{
        String code = json.get("code").asText();
        RestTemplate rt = new RestTemplate();

        HttpHeaders TokenHeaders = new HttpHeaders();
        TokenHeaders.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
        MultiValueMap<String, String> accessToken = new LinkedMultiValueMap<>();
        accessToken.add("grant_type", "authorization_code");
        accessToken.add("client_id", client_id);
        accessToken.add("redirect_uri", "http://localhost:8080/callback2.html");
        accessToken.add("client_secret", client_secret);
        accessToken.add("code",code);

        HttpEntity<MultiValueMap<String, String>> TokenRequest = new HttpEntity<>(accessToken ,TokenHeaders);

        ResponseEntity<String> TokenResponse = rt.exchange(
                "https://kauth.kakao.com/oauth/token",
                HttpMethod.POST,
                TokenRequest,
                String.class
        );
        ObjectMapper objectMapper = new ObjectMapper();
        KakaoToken kakaoToken = null;
        try{
            kakaoToken = objectMapper.readValue(TokenResponse.getBody(), KakaoToken.class);
            System.out.println(TokenResponse);
        }catch (JsonProcessingException e){
            throw new RuntimeException(e);
        }
        return kakaoData(kakaoToken.getAccess_token());
    }
    private ResponseEntity<?> kakaoData(String accessToken){
        RestTemplate rt = new RestTemplate();
        HttpHeaders TokenHeaders = new HttpHeaders();
        TokenHeaders.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
        TokenHeaders.add("Authorization", "Bearer " + accessToken);
        HttpEntity<MultiValueMap<String, String>> TokenRequest = new HttpEntity<>(TokenHeaders);
        ResponseEntity<String> TokenResponse = rt.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.GET,
                TokenRequest,
                String.class
        );
        System.out.println(TokenResponse);
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = null;
        try{
            jsonNode = objectMapper.readTree(TokenResponse.getBody());
        }catch (JsonProcessingException e){
            e.printStackTrace();
        }
        KakaoData kakaoData = new KakaoData();
        kakaoData.setId(jsonNode.path("id").asText());
        kakaoData.setNickname(jsonNode.path("properties").path("nickname").asText());
        kakaoData.setEmail(jsonNode.path("kakao_account").path("email").asText());
        Optional<UserEntity> optionalUser1 = userRepository.findBySocialTypeAndSocialId(SocialType.KAKAO, kakaoData.getId());
        Optional<UserEntity> optionalUser2 = userRepository.findByEmail(kakaoData.getEmail());
        if(optionalUser1.isPresent()){
            UserEntity returnUser = optionalUser1.get();
            Long idx = returnUser.getIdx();
            String role = returnUser.getRole().toString();
            String nickname = returnUser.getNickname();
            String userProfile = returnUser.getUserProfile();
            String KakaoaccessToken = jwtService.createAccessToken(idx,nickname,userProfile);
            String KakaoRefreshToken = jwtService.createRefreshToken();
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.add("Authorization", "Bearer " + KakaoaccessToken);
            responseHeaders.add("Authorization-Refresh", KakaoRefreshToken);
            returnUser.setRefreshToken(KakaoRefreshToken);
            userRepository.save(returnUser);
            log.info("로그인 성공");
            log.info(KakaoaccessToken);
        }else if(optionalUser2.isPresent()){
            log.info("이미 사용중인 이메일");
            String socialType = String.valueOf(optionalUser2.get().getSocialType());
            if(socialType.equals("NAVER")) {
                log.info("카카오로 이미 가입된 이메일");
                return new ResponseEntity<>("이미 네이버로 가입된 이메일 입니다.", HttpStatus.IM_USED);
            }
            }else{
                //회원가입
            UserEntity user = UserEntity.builder()
                    .email(kakaoData.getEmail())
                    .nickname(kakaoData.getNickname())
                    .socialType(SocialType.KAKAO)
                    .socialId(kakaoData.getId())
                    .role(Role.USER)
                    .isGoodseul(null)
                    .build();
            userRepository.save(user);
                log.info("카카오 계정 없음");
                return new ResponseEntity<>(kakaoData, HttpStatus.ACCEPTED);
            }
            return new ResponseEntity<>(accessToken, HttpStatus.ACCEPTED);
        }
    }















