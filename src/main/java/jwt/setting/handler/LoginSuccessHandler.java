package jwt.setting.handler;

import data.entity.UserEntity;
import data.repository.UserRepository;
import jwt.setting.settings.JwtService;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@RequiredArgsConstructor
public class LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final JwtService jwtService;
    private final UserRepository userRepository;

    @Value("${jwt.access.expiration}")
    private String accessTokenExpiration;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) {
        String id = extractUsername(authentication);// 인증 정보에서 Username(email) 추출
        UserEntity user = userRepository.findByIdx(Long.parseLong(id)).get();
        long idx = user .getIdx();
        String nickname = user .getNickname().toString();
        String accessToken = jwtService.createAccessToken(idx,nickname);// JwtService의 createAccessToken을 사용하여 AccessToken 발급
        String refreshToken = jwtService.createRefreshToken(); // JwtService의 createRefreshToken을 사용하여 RefreshToken 발급
        jwtService.sendAccessAndRefreshToken(response,accessToken,refreshToken); // 응답 헤더에 AccessToken, RefreshToken 실어서 응답

        userRepository.findByIdx(Long.parseLong(id))
                .ifPresent(userEntity -> {
                    userEntity.updateRefreshToken(refreshToken);
                    userRepository.saveAndFlush(userEntity);
                });
        log.info("로그인에 성공하였습니다. 이메일 : {}", idx);
        log.info("로그인에 성공하였습니다. AccessToken : {}",accessToken);
        log.info("re: "+ refreshToken);
        log.info("발급된 AccessToken 만료 기간 : {}", accessTokenExpiration);
    }
    private String extractUsername(Authentication authentication){
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return userDetails.getUsername();
    }
}



















