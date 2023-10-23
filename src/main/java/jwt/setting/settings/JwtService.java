package jwt.setting.settings;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import data.exception.TokenException;
import data.exception.UnauthenticatedUserException;
import data.repository.UserRepository;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Data
@Slf4j
public class JwtService {
    //이 밑의 속성들은 application.yml 파일에서 설정된 값들로 주입된다.

    @Value("${jwt.secretKey}")
    private String secretKey;

    @Value("${jwt.access.expiration}") // 액세스 토큰의 만료 기간
    private Long accessTokenExpirationPeriod;

    @Value("${jwt.refresh.expiration}") //리플레스 토큰의 만료 기간
    private Long refreshTokenExpirationPeriod;

    @Value("${jwt.access.header}") // HTTP 응답 헤더에 토큰을 실어 보낼 때 사용될 헤더 이름
    private String accessHeader;

    @Value("${jwt.refresh.header}") // HTTP 응답 헤더에 토큰을 실어 보낼 때 사용될 헤더 이름
    private String refreshHeader;

    /**
     * JWT의 Subject와 Claim으로 email 사용 -> 클레임의 name을 "email"으로 설정
     * JWT의 헤더에 들어오는 값 : 'Authorization(Key) = Bearer {토큰} (Value)' 형식
     */
    private static final String ACCESS_TOKEN_SUBJECT = "AccessToken";
    private static final String REFRESH_TOKEN_SUBJECT = "RefreshToken";
    private static final String IDX_CLAIM = "idx";
    private static final String NICKNAME_CLAIM = "nickname";
    private static final String USERPROFILE_CLAIM = "userProfile";
    private static final String BEARER = "Bearer ";

    private final UserRepository userRepository;

    //AccessToken 생성 메소드
    //주어진 사용자 이메일을 기반으로 액세스 토큰을 생성하는 메소드이다. JWT 빌더를 사용하여 토큰을 생성하고, 토큰의 subject와 만료 시간을 설정
    public String createAccessToken(long idx, String nickname, String userProfile) {
        Date now = new Date();
        return JWT.create() //JWT 토큰을 생성하는 빌더 반환
                .withSubject(ACCESS_TOKEN_SUBJECT)//JWT의 Subject 지정 ->  AccessToken이므로 AccessToken
                .withExpiresAt(new Date(now.getTime() + accessTokenExpirationPeriod)) // 토큰 만료 시간 설정
                //클레임으로는 저희는 email 하나만 사용
                //추가적으로 식별자나, 이름 등의 정보를 더 추가해도됨
                //추가할 경우 .withClaim(클래임 이름, 클래임 값)으로 설정해주면 됨
                .withClaim(IDX_CLAIM, idx)
                .withClaim(NICKNAME_CLAIM, nickname)
                .withClaim(USERPROFILE_CLAIM, userProfile)
                .sign(Algorithm.HMAC512(secretKey)); //HMAC512 알고리즘 사용, application-yml에서 지정한 secret 키로 암호화
    }
    //RefreshToken 생성
    //RefreshToken은 Claim에 email도 넣지 않으므로 withClaim() X
    public String createRefreshToken() {
        Date now = new Date();
        return JWT.create()
                .withSubject(REFRESH_TOKEN_SUBJECT)
                .withExpiresAt(new Date(now.getTime() + refreshTokenExpirationPeriod))
                .sign(Algorithm.HMAC512(secretKey));
    }
    //AccessToken 헤더에 실어서 보내기
    public void sendAccessToken(HttpServletResponse response, String accessToken){
        response.setStatus(HttpServletResponse.SC_OK);
        response.setHeader(accessHeader, accessToken);
        log.info("재발급된 Access Token : {}", accessToken);
    }
    //AccessToken + RefreshToken 헤더에 실어서 보내기
    public void sendAccessAndRefreshToken(HttpServletResponse response, String accessToken, String refreshToken){
            response.setStatus(HttpServletResponse.SC_OK);
            setAccessHeader(response, accessToken);
            setRefreshHeader(response, refreshToken);
        log.info("Access Token, Refresh Token 헤더 설정 완료");
    }
    //extractRefreshToken(), extractAccessToken(), extractEmail(): HTTP 요청 헤더에서 토큰을 추출하고, 토큰을 검증하고, 클레임(claim)에서 이메일을 추출하는 메서드입니다.

    //헤더에서 RefreshToken 추출
    // 토큰 형식 : Bearer XXX에서 Bearer를 제외하고 순수 토큰만 가져오기 위해서
    // 헤더를 가져온 후 "Bearer"를 삭제(""로 replace)
    public Optional<String> extractRefreshToken(HttpServletRequest request){
        return Optional.ofNullable(request.getHeader(refreshHeader)) // 리프레시 토큰 가져옴
                .filter(refreshToken -> refreshToken.startsWith(BEARER)) //토큰이 Bearer로 시작하는지 확인
                .map(refreshToken -> refreshToken.replace(BEARER, "")); // map을 사용하여 Bearer부분을 삭제하고 순수한 토큰 문자열만을 남긴다.
                //이렇게 처리된 토큰 문자열이 Optional 객체로 감싸져 반환됨
    }
    /**
     * 헤더에서 AccessToken 추출
     * 토큰 형식 : Bearer XXX에서 Bearer를 제외하고 순수 토큰만 가져오기 위해서
     * 헤더를 가져온 후 "Bearer"를 삭제(""로 replace)
     */
    public Optional<String> extractAccessToken(HttpServletRequest request){
        return Optional.ofNullable(request.getHeader(accessHeader))
                .filter(refreshToken -> refreshToken.startsWith(BEARER))
                .map(refreshToken -> refreshToken.replace(BEARER, ""));
    }
    /**
     * AccessToken에서 Email 추출
     * 추출 전에 JWT.require()로 검증기 생성
     * verify로 AceessToken 검증 후
     * 유효하다면 getClaim()으로 이메일 추출
     * 유효하지 않다면 빈 Optional 객체 반환
     */
//    public Optional<String> extractEmail(String accessToken){
//        try {
//            //토큰 유효성 검사하는 데에 사용할 알고리즘이 있는 JWT verifier builder 반환
//            return Optional.ofNullable(JWT.require(Algorithm.HMAC512(secretKey))
//                    .build()//반환된 빌더로 JWT verifier 생성
//                    .verify(accessToken)//accessToken을 검증하고 유효하지 않다면 예외 발생
//                    .getClaim(EMAIL_CLAIM)//claim(Email)가져오기
//                    .asString()); // 클레임 값을 문자열로 변환한 후, 이 값을 'Optional'객체로 감싸서 반환
//        }catch (Exception e) {
//            log.error("액세스 토큰이 유효하지 않습니다");
//            return Optional.empty();
//        }
//    }

    public Optional<Long> extractIdx(String accessToken) {
        try {
            return Optional.ofNullable(JWT.require(Algorithm.HMAC512(secretKey))
                    .build()
                    .verify(accessToken)
                    .getClaim(IDX_CLAIM)
                    .asLong());
        } catch (Exception e){
            log.error("액세스 토큰이 유효하지않습니다.");
            return Optional.empty();
        }
    }

    public Long extractIdxFromRequest(HttpServletRequest request) {

        String accessToken = extractAccessToken(request).orElseThrow(TokenException::new);

        try {
            return JWT.require(Algorithm.HMAC512(secretKey))
                    .build()
                    .verify(accessToken)
                    .getClaim(IDX_CLAIM)
                    .asLong();
        } catch (Exception e){
            log.error("액세스 토큰이 유효하지않습니다.");
            throw new UnauthenticatedUserException();
        }
    }

    public Optional<String> extractNickname(String accessToken){
        try{
            return Optional.ofNullable(JWT.require(Algorithm.HMAC512(secretKey))
                    .build()
                    .verify(accessToken)
                    .getClaim(NICKNAME_CLAIM)
                    .asString());
        }catch (Exception e){
            log.error("엑세스 토큰이 유효하지 않습니다");
            return Optional.empty();
        }
    }

    public Optional<String> extractUserProfile(String accessToken){
        try{
            return Optional.ofNullable(JWT.require(Algorithm.HMAC512(secretKey))
                    .build()
                    .verify(accessToken)
                    .getClaim(USERPROFILE_CLAIM)
                    .asString());
        }catch (Exception e){
            log.error("엑세스 토큰이 유효하지 않습니다");
            return Optional.empty();
        }
    }
    //AccessToken 헤더 설정
    public void setAccessHeader(HttpServletResponse response, String accessToken){
        response.setHeader(accessHeader, accessToken);

    }
    //RefreshToken 헤더 설정
    public void setRefreshHeader(HttpServletResponse response, String refreshToken){
        response.setHeader(refreshHeader,refreshToken);
    }

    //RefreshToken DB 저장 (업데이트)
    public void updateRefreshToken(String email, String refreshToken){
        userRepository.findByEmail(email) // 주어진 이메일로 사용자를 찾아서 리프레시 토큰을 업데이트
                .ifPresentOrElse(
                        user -> user.updateRefreshToken(refreshToken),
                        () -> new Exception("일치하는 회원이 없습니다")
                );
    }

    //refreshToken 삭제 (로그아웃)
    public void logout(Long idx) {
        userRepository.removeRefreshTokenByIdx(idx);
        log.info("{} 사용자의 refresh token이 제거되었습니다.", idx);
    }

    //주어진 토큰이 유효한지 검증하는 메서드
    public boolean isTokenValid(String token) {
        try {
            //JWT.require로 토큰 유효성을 검사하는 로직이 있는 JWT verifier builder를 반환한다.
            //그 후 반환된 builder를 사용하여 .verify(accessToken)로 Token을 검증
            JWT.require(Algorithm.HMAC512(secretKey)).build().verify(token);
            return true;
        }catch (Exception e){
            log.error("유효하지 않은 토큰입니다. {}",e.getMessage());
            return false;
        }
    }
}






























