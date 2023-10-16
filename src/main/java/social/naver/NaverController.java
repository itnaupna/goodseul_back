package social.naver;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
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
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@CrossOrigin
@RestController
public class NaverController {
    @Value("${spring.security.oauth2.client.registration.naver.client-id}")
    private String client_id;
    @Value("${spring.security.oauth2.client.registration.naver.client-secret}")
    private String client_secret;
    @Value("${spring.security.oauth2.client.provider.naver.token-uri}")
    private String token_uri;
    @Value("${spring.security.oauth2.client.provider.naver.authorization-uri}")
    private String a_uri;
    @Value("${spring.security.oauth2.client.registration.naver.redirect-uri}")
    private String redirect_uri;

    private final UserRepository userRepository;
    private final JwtService jwtService;

    public NaverController(UserRepository userRepository, JwtService jwtService) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    @PostMapping("/api/lv0/naver")
    //access 코드 발급
    private ResponseEntity<?> naverCallBack(@RequestBody JsonNode json, HttpServletResponse response) throws JsonProcessingException {
        // RestTemplate 인스턴스 생성
        String code = json.get("code").asText();
        RestTemplate rt = new RestTemplate();

        HttpHeaders accessTokenHeaders = new HttpHeaders();
        accessTokenHeaders.add("Content-type", "application/x-www-form-urlencoded");
        //access 발급 요청
        MultiValueMap<String, String> accessToken = new LinkedMultiValueMap<>();
        accessToken.add("grant_type", "authorization_code");
        accessToken.add("client_id", client_id);
        accessToken.add("redirect_uri", "http://localhost:8080/callback.html");
        accessToken.add("client_secret", client_secret);
        accessToken.add("code", code);    // 응답으로 받은 코드

        HttpEntity<MultiValueMap<String, String>> accessTokenRequest = new HttpEntity<>(accessToken, accessTokenHeaders);

        ResponseEntity<String> accessTokenResponse = rt.exchange(
                "https://nid.naver.com/oauth2.0/token",
                HttpMethod.POST,
                accessTokenRequest,
                String.class
        );
        ObjectMapper objectMapper = new ObjectMapper();
        NaverToken naverToken = null;
        try {
            naverToken = objectMapper.readValue(accessTokenResponse.getBody(), NaverToken.class);
            System.out.println(accessTokenResponse);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return naverData(naverToken.getAccess_token());
    }

    private ResponseEntity<?> naverData(String accessToken) {
        RestTemplate profile_rt = new RestTemplate();
        HttpHeaders userDetailReqHeaders = new HttpHeaders();
        userDetailReqHeaders.add("Content-type", "application/x-www-form-urlencoded;charset=UTF-8");
        userDetailReqHeaders.add("Authorization", "Bearer " + accessToken);
        HttpEntity<MultiValueMap<String, String>> naverProfileRequest = new HttpEntity<>(userDetailReqHeaders);
        // 서비스서버 - 네이버 인증서버 : 유저 정보 받아오는 API 요청
        ResponseEntity<String> userDetailResponse = profile_rt.exchange(
                "https://openapi.naver.com/v1/nid/me",
                HttpMethod.GET,
                naverProfileRequest,
                String.class
        );
        System.out.println(userDetailResponse);
        // 네이버로부터 받은 정보를 객체화
        // *이때, 공식문서에는 응답 파라미터에 mobile 밖에없지만, 국제전화 표기로 된 mobile_e164도 같이 옴. 따라서 NaverProfileVo에 mobile_e164 필드도 있어야 정상적으로 객체가 생성됨
        ObjectMapper profile_om = new ObjectMapper();
        NaverData naverData = null;
        try {
            JsonNode jsonNode =profile_om.readTree(userDetailResponse.getBody());
            naverData = profile_om.treeToValue(jsonNode.get("response"), NaverData.class);
        } catch (JsonProcessingException je) {
            je.printStackTrace();
        }
        Optional<UserEntity> optionalUser1 = userRepository.findBySocialTypeAndSocialId(SocialType.NAVER, naverData.getId());
        Optional<UserEntity> optionalUser2 = userRepository.findByEmail(naverData.getEmail());
        if (optionalUser1.isPresent()) {
            UserEntity returnUser = optionalUser1.get();
            Long idx = returnUser.getIdx();
            String role = returnUser.getRole().toString();
            String nickname = returnUser.getNickname().toString();
            String email = returnUser.getEmail().toString();
            String userProfile = returnUser.getUserProfile().toString();
            String NaveraccessToken = jwtService.createAccessToken(idx,nickname,userProfile);
            String NaverRefreshToken = jwtService.createRefreshToken();
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.add("Authorization", "Bearer " + NaveraccessToken);
            responseHeaders.add("Authorization-Refresh", NaverRefreshToken);
            Map<String, String> responseBody = new HashMap<>();
            responseBody.put("email", email);
            returnUser.setRefreshToken(NaverRefreshToken);
            userRepository.save(returnUser);
            log.info("로그인 성공");
            log.info(NaveraccessToken);
            return new ResponseEntity<>(responseBody, responseHeaders, HttpStatus.OK);
        } else if (optionalUser2.isPresent()) {
            log.info("이미 사용중인 이메일");
            String socialType = String.valueOf(optionalUser2.get().getSocialType());
            if (socialType.equals("KAKAO")) {
                log.info("카카오로 이미 가입된 이메일.");
                return new ResponseEntity<>("이미 카카오로 가입된 이메일 입니다.", HttpStatus.IM_USED);
            } else {
                log.info("일반회원으로 이미 가입된 이메일");
                return new ResponseEntity<>("이미 일반회원으로 가입된 이메일입니다.", HttpStatus.IM_USED);
            }
        }else {
            //회원가입
            UserEntity user = UserEntity.builder()
                    .email(naverData.getEmail())
                    .name(naverData.getName())
                    .phoneNumber(naverData.getMobile().replace("-",""))
                    .socialType(SocialType.NAVER)
                    .socialId(naverData.getId())
                    .role(Role.USER)
                    .isGoodseul(null)
                    .build();
            userRepository.save(user);
            return new ResponseEntity<>(naverData, HttpStatus.ACCEPTED);
        }

    }
    private void deleteToken(String accessToken){
        RestTemplate rt = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=UTF-8");
        MultiValueMap<String, String> param = new LinkedMultiValueMap<>();
        param.add("grant_type", "delete");
        param.add("client_id", client_id);
        param.add("client_secret", client_secret);
        param.add("access_token", accessToken);
        param.add("service_provider","NAVER");
        HttpEntity<MultiValueMap<String, String>> naverTokenRequest = new HttpEntity<>(param, headers);
        ResponseEntity<String> response = rt.exchange(
                "https://nid.naver.com/oauth2.0/token",
                HttpMethod.POST,
                naverTokenRequest,
                String.class
        );
        log.info("네이버 로그아웃");
    }
}
