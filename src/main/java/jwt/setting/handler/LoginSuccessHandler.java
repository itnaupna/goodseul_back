package jwt.setting.handler;

import data.entity.GoodseulEntity;
import data.entity.UserEntity;
import data.repository.GoodseulRepository;
import data.repository.UserRepository;
import data.service.OnlineUserService;
import jwt.setting.config.Role;
import jwt.setting.settings.JwtService;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@RequiredArgsConstructor
public class LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final OnlineUserService onlineUserService;

    @Value("${jwt.access.expiration}")
    private String accessTokenExpiration;

    @Override
    @Transactional
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) {
        String id = extractUsername(authentication);// 인증 정보에서 Username(email) 추출
        UserEntity user = userRepository.findByEmail(id).get();
        long idx = user .getIdx();
        String nickname = user .getNickname().toString();
        String userProfile = user.getUserProfile().toString();
        String accessToken  = jwtService.createAccessToken(idx,nickname,userProfile);// JwtService의 createAccessToken을 사용하여 AccessToken 발급
        String refreshToken = jwtService.createRefreshToken(); // JwtService의 createRefreshToken을 사용하여 RefreshToken 발급
        jwtService.sendAccessAndRefreshToken(response,accessToken,refreshToken); // 응답 헤더에 AccessToken, RefreshToken 실어서 응답

        userRepository.findByEmail(id)

                .ifPresent(userEntity -> {
                    userEntity.updateRefreshToken(refreshToken);
                    userRepository.saveAndFlush(userEntity);

                    if(userEntity.getRole() == Role.GOODSEUL) {
                        onlineUserService.addAllUser(userEntity);
                        onlineUserService.logOnlineGoodsuleUsers();
                    }

                });

        log.info("로그인에 성공하였습니다. 이메일 : {}", id);
        log.info("로그인에 성공하였습니다. AccessToken : {}",accessToken);
        log.info("re: "+ refreshToken);
        log.info("발급된 AccessToken 만료 기간 : {}", accessTokenExpiration);
    }
    private String extractUsername(Authentication authentication){
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return userDetails.getUsername();
    }

}



















