package data.service;

import data.dto.GoodseulDto;
import data.dto.GoodseulInfoDto;
import data.dto.MyPageResponseDto;
import data.dto.UserDto;
import data.entity.ChatRoomEntity;
import data.entity.GoodseulEntity;
import data.entity.UserEntity;
import data.exception.GoodseulNotFoundException;
import data.exception.UserNotFoundException;
import data.repository.*;
import data.service.file.StorageService;
import jwt.setting.config.Role;

import jwt.setting.config.SocialType;
import jwt.setting.handler.LoginSuccessHandler;
import jwt.setting.settings.JwtService;
import lombok.extern.slf4j.Slf4j;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;

import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
@Slf4j
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final GoodseulRepository goodseulRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserCouponRepository userCouponRepository;
    private final PointHistoryRepository pointHistoryRepository;
    private final ReviewRepository reviewRepository;
    private final FavoriteRepository favoriteRepository;
    private final ChatRoomRepository chatRoomRepository;

    private final JwtService jwtService;
    private final DefaultMessageService messageService;
    private final StorageService storageService;
    private final String USER_PROFILE_PATH="userprofile";
    private final String CAREER_PATH="career";

    public UserService(UserRepository userRepository, GoodseulRepository goodseulRepository, PasswordEncoder passwordEncoder, CouponRepository couponRepository, UserCouponRepository userCouponRepository, PointHistoryRepository pointHistoryRepository, ReviewRepository reviewRepository, FavoriteRepository favoriteRepository, ChatRoomRepository chatRoomRepository, JwtService jwtService, StorageService storageService) {
        this.userRepository = userRepository;
        this.goodseulRepository = goodseulRepository;
        this.passwordEncoder = passwordEncoder;
        this.userCouponRepository = userCouponRepository;
        this.pointHistoryRepository = pointHistoryRepository;
        this.reviewRepository = reviewRepository;
        this.favoriteRepository = favoriteRepository;
        this.chatRoomRepository = chatRoomRepository;
        this.jwtService = jwtService;
        this.storageService = storageService;
        this.messageService = NurigoApp.INSTANCE.initialize("NCSRLOLZ8HFH6SU6","GGK9IOYOQN3SHLF4P8X6VBNOILRNWPXV","https://api.coolsms.co.kr");
    }

    //일반 회원 회원가입
    public void signUp(UserDto userDto) throws Exception {
        if (userRepository.findByEmail(userDto.getEmail()).isPresent()) {
            throw new Exception("이미 존재하는 이메일입니다");
        }
        if (userRepository.findByNickname(userDto.getNickname()).isPresent()) {
            throw new Exception("이미 존재하는 닉네임입니다.");
        }
        UserEntity user = UserEntity.builder()
                .email(userDto.getEmail())
                .password(userDto.getPassword())
                .nickname(userDto.getNickname())
                .name(userDto.getName())
                .phoneNumber(userDto.getPhoneNumber())
                .location(userDto.getLocation())
                .birth(userDto.getBirth())
                .isGoodseul(null)
                .role(Role.USER)
                .build(); // 최종적으로 객체를 반환
        user.passwordEncode(passwordEncoder); // 사용자 비밀번호를 암호화하기 위한 Spring Security의 비밀번호 인코딩
        userRepository.save(user); // 새 사용자를 DB에 저장
    }

    //구슬님 회원가입
    public void goodseulSignup(GoodseulDto goodseulDto, UserDto userDto, List<MultipartFile> uploads) throws Exception{

        StringBuilder sb = new StringBuilder();

        for(MultipartFile upload : uploads) {
            String fileName = storageService.saveFile(upload, CAREER_PATH);
            sb.append(fileName).append(",");
        }

        log.info(userDto.getEmail());
        log.info(userDto.getNickname());

        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new Exception("이미 존재하는 이메일입니다");
        }
        if (userRepository.existsByNickname(userDto.getNickname())) {
            throw new Exception("이미 존재하는 닉네임입니다.");
        }

        GoodseulEntity goodseuls = GoodseulEntity.builder()
                .skill(goodseulDto.getSkill())
                .career(sb.substring(0,sb.length()-1))
                .goodseulName(goodseulDto.getGoodseulName())
                .isPremium(goodseulDto.getIsPremium())
                .premiumDate(goodseulDto.getPremiumDate())
                .build();
        goodseulRepository.save(goodseuls);
        UserEntity user = UserEntity.builder()
                .email(userDto.getEmail())
                .password(userDto.getPassword())
                .nickname(userDto.getNickname())
                .name(userDto.getName())
                .phoneNumber(userDto.getPhoneNumber())
                .location(userDto.getLocation())
                .birth(userDto.getBirth())
                .role(Role.GOODSEUL)
                .build(); // 최종적으로 객체를 반환
        user.setIsGoodseul(goodseuls);
        user.passwordEncode(passwordEncoder); // 사용자 비밀번호를 암호화하기 위한 Spring Security의 비밀번호 인코딩
        userRepository.save(user); // 새 사용자를 DB에 저장
    }

    //회원정보 페이징
    public Page<UserEntity> userPaging(int page){
        PageRequest pageable = PageRequest.of(page, 5, Sort.by(Sort.Direction.ASC, "idx"));
        return userRepository.findAll(pageable);
    }

    //회원정보 리스트
    public List<UserDto> userList(){
    List<UserDto> list = new ArrayList<>();
    for(UserEntity entity : userRepository.findAll()){
        list.add(UserDto.toUserDto(entity));
    }
    return list;
    }

    //구슬님 지역별 페이징
    public Page<GoodseulDto> goodseulPaging(String location,int page){
        PageRequest pageable = PageRequest.of(page, 5,Sort.by(Sort.Direction.ASC,"idx"));
        return userRepository.findGoodseulIdxByLocation(location, pageable);
    }

    //목적별 리스트
    public Page<GoodseulDto> skillList(String skill, int page){
        PageRequest pageable = PageRequest.of(page, 5, Sort.by(Sort.Direction.ASC,"idx"));
        return goodseulRepository.findGoodseulIdxBySkill(skill, pageable);
    }

    //구슬님 리스트
    public List<GoodseulDto> goodseulList(){
        List<GoodseulDto> list = new ArrayList<>();
        for(GoodseulEntity entity : goodseulRepository.findAll()){
            list.add(GoodseulDto.toGoodseulDto(entity));
        }
        return list;
    }
    //닉네임,이메일,전화번호 유효성 검사
    public String allCheck(String email, String birth, String name) {
        Optional<UserEntity> user = userRepository.findByEmailAndBirthAndName(email, birth, name);
        if (user.isPresent()) {
            return user.get().getPhoneNumber();
        }
        return "없습니다";
    }

    //비밀번호 변경
    public String pwdUpdate(String email, String password) {
        Optional<UserEntity> userId = userRepository.findByEmail(email);
        if (userId.isPresent()) {
            userId.get().setPassword(passwordEncoder.encode(password));
            userRepository.save(userId.get());
            return "완료";
        } else {
            return "해당 회원이 없습니다.";
        }
    }

    //이메일 유효성 검사
    public boolean emailCheck(String email) {
        return userRepository.existsByEmail(email);
    }

    //닉네임 유효성 검사
    public boolean nicknameCheck(String nickname){
       return userRepository.existsByNickname(nickname);
    }
    // 핸드폰 번호 유효성 검사
    public boolean phoneCheck(String phoneNumber) {
        return userRepository.existsByPhoneNumber(phoneNumber);
    }
    
    //문자 인증번호 보내기
    public String sendSms(String phoneNumber) {
        Optional<UserEntity> phoneNumberCheck = userRepository.findByPhoneNumber(phoneNumber);
        if(!phoneNumberCheck.isPresent()){
            return "없습니다";
        }
        phoneNumberCheck.get().setPhoneNumber(phoneNumber);
        String authnum = generateRandomNumber(4);
        Message message = new Message();
        message.setFrom("01076331961");
        message.setTo(phoneNumber);
        message.setText("인증번호는 [" + authnum + "] 입니다");
        SingleMessageSentResponse response = this.messageService.sendOne(new SingleMessageSendingRequest(message));
        return authnum;
    }

    //인증번호 생성
    public static String generateRandomNumber(int digits) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder(digits);

        for (int i = 0; i < digits; i++) {
            int digit = random.nextInt(10);
            sb.append(digit);
        }
        return sb.toString();
    }

    public GoodseulInfoDto getGoodseulInfo(long idx, HttpServletRequest request) {
        long u_idx;
        boolean favoriteStatus;

        try {
            u_idx = jwtService.extractIdxFromRequest(request);
            favoriteStatus = favoriteRepository.countByUserEntity_idxAndGoodseulEntity_idx(u_idx, idx) > 0;

        } catch (Exception e) {
            u_idx = 0;
            favoriteStatus = false;
        }

        GoodseulInfoDto goodseulInfoDto = new GoodseulInfoDto();
        GoodseulEntity goodseulEntity = goodseulRepository.findByIdx(idx).orElseThrow(GoodseulNotFoundException::new);
        goodseulInfoDto.setGoodseulDto(GoodseulDto.toGoodseulDto(goodseulEntity));
        goodseulInfoDto.setUserDto(UserDto.toUserDto(userRepository.findByIsGoodseul_Idx(goodseulEntity.getIdx()).orElseThrow(UserNotFoundException::new)));
        goodseulInfoDto.setFavoriteStatus(favoriteStatus);
        return goodseulInfoDto;

    }

//    //회원 탈퇴
//    public void deleteUser(Long idx){
//        userRepository.deleteAllByIdx(idx);
//    }

    //회원 업데이트
    public void updateUser(HttpServletRequest request, UserDto userDto){
        Long userIdx = jwtService.extractIdx(jwtService.extractAccessToken(request).orElseThrow(() -> new RuntimeException("Access Token이 존재하지 않습니다.")))
                .orElseThrow(() -> new RuntimeException("사용자 인덱스를 찾을 수 없습니다."));
        UserEntity user = userRepository.findByIdx(userIdx)
                .orElseThrow(() -> new RuntimeException("사용자를 데이터베이스에서 찾을 수 없습니다."));

        if(!user.getIdx().equals(userIdx)){
            log.error("불일치");
            throw new RuntimeException("수정 권한이 없습니다");
        }
        user.setBirth(userDto.getBirth());
        user.setLocation(userDto.getLocation());
        user.setPhoneNumber(userDto.getPhoneNumber());
        user.setNickname(userDto.getNickname());
        user.setName(userDto.getName());
        userRepository.save(user);
    }

    //구슬 업데이트
    public void updateGoodseul(HttpServletRequest request, GoodseulDto goodseulDto){
        Long userIdx = jwtService.extractIdx(jwtService.extractAccessToken(request).orElseThrow(() -> new RuntimeException("Access Token이 존재하지 않습니다.")))
                .orElseThrow(() -> new RuntimeException("사용자 인덱스를 찾을 수 없습니다."));
        UserEntity user = userRepository.findByIdx(userIdx)
                .orElseThrow(() -> new RuntimeException("사용자를 데이터베이스에서 찾을 수 없습니다."));
        GoodseulEntity goodseul = goodseulRepository.findByIdx(user.getIsGoodseul().getIdx()).get();
        goodseul.setCareer(goodseulDto.getCareer());
        goodseul.setGoodseulName(goodseulDto.getGoodseulName());
        goodseul.setGoodseulInfo(goodseulDto.getGoodseulInfo());
        goodseul.setSkill(goodseulDto.getSkill());

        goodseulRepository.save(goodseul);
    }

    public String findByEmail (String name, String phone, String birth) {
        Optional<UserEntity> email = userRepository.findByNameAndPhoneNumberAndBirth(name, phone, birth);
        return email.map(UserEntity::getEmail)
                .orElseThrow(EntityNotFoundException::new);
    }

    // 회원 탈퇴
    public boolean signOut (HttpServletRequest request) {
        long idx = jwtService.extractIdx(jwtService.extractAccessToken(request).get()).get();
        log.info(idx + "= idx");
        try {
            Optional<UserEntity> entity = userRepository.findById(idx);
            if(entity.isPresent()) {
                singOut(entity.get());

                return true;
            }
        } catch (Exception e) {
            log.info("탈퇴 실패" + e.getMessage());
            return false;
        }
        return false;
    }
    private void singOut(UserEntity user) {
        user.setEmail("");
        user.setName(user.getName() + "(탈퇴)");
        user.setPassword("");
        user.setBirth("");
        user.setLocation("");
        user.setPhoneNumber("");
        user.setRefreshToken("");
        user.setSocialId("");

        if(user.getIsGoodseul() != null) {
            GoodseulEntity goodseul = goodseulRepository.findByIdx(user.getIsGoodseul().getIdx()).get();
            goodseul.setCareer("");
            goodseul.setGoodseulName(goodseul.getGoodseulName() + "(탈퇴)");
            goodseul.setGoodseulProfile("");
            goodseul.setIsPremium(null);
            goodseul.setPremiumDate(null);
            goodseul.setSkill("");
        }

        userRepository.save(user);
    }

    //사용자 프로필 사진 업데이트
    public String updatePhoto(MultipartFile upload, HttpServletRequest request) throws IOException {
        String fileName = storageService.saveFile(upload, USER_PROFILE_PATH);
        Long idx = jwtService.extractIdx(jwtService.extractAccessToken(request).get()).get();
        UserEntity user = userRepository.findByIdx(idx).get();
        user.setUserProfile(fileName);
        userRepository.save(user);
        return fileName;
    }

    public String getUserEmail(HttpServletRequest request) {
        long userIdx = jwtService.extractIdxFromRequest(request);
        return userRepository.findByIdx(userIdx).orElseThrow(UserNotFoundException::new).getEmail();
    }

    public MyPageResponseDto getMypageData(HttpServletRequest request) {
        long userIdx = jwtService.extractIdxFromRequest(request);
        MyPageResponseDto dto = new MyPageResponseDto();

        String email = userRepository.findByIdx(userIdx).orElseThrow(UserNotFoundException::new).getEmail();
        int couponCount = userCouponRepository.countAllByUserEntity_Idx(userIdx);
        int myPoint = pointHistoryRepository.findTotalPointsByMemberIdx(userIdx);
        int reviewCount = reviewRepository.countAllByUserEntity_Idx(userIdx);
        int favoriteCount = favoriteRepository.countAllByUserEntity_Idx(userIdx);
        int chatRoomCount = 0;

        dto.setEmail(email);
        log.info("Email : {}",email);

        dto.setCouponCount(couponCount);
        log.info("Coupon Count : {}",couponCount);

        dto.setMyPoint(myPoint);
        log.info("Point : {}",myPoint);

        dto.setReviewCount(reviewCount);
        log.info("Review Count : {}", reviewCount);

        dto.setFavoriteCount(favoriteCount);
        log.info("Favorite Count : {}", favoriteCount);

        List<ChatRoomEntity> list = chatRoomRepository.findByRoomIdStartingWithOrRoomIdEndingWithOrderByLastChatTimeDesc(userIdx + "to", "to" + userIdx);
        chatRoomCount = list.size();
        dto.setChatRoomCount(chatRoomCount);
        log.info("ChatRoom Count : {}",chatRoomCount);

        return dto;
    }

}
