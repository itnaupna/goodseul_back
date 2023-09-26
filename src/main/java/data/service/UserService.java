package data.service;

import data.dto.UserDto;
import data.entity.UserEntity;
import data.repository.UserRepository;
import jwt.setting.config.Role;

import lombok.extern.slf4j.Slf4j;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import javax.transaction.Transactional;
import java.util.Optional;
import java.util.Random;

@Service
@Slf4j
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final DefaultMessageService messageService;


    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.messageService = NurigoApp.INSTANCE.initialize("NCSRLOLZ8HFH6SU6","GGK9IOYOQN3SHLF4P8X6VBNOILRNWPXV","https://api.coolsms.co.kr");
    }

    //회원가입
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
                .isGoodseul(userDto.getIsGoodseul())
                .role(Role.USER)
                .build(); // 최종적으로 객체를 반환
        user.passwordEncode(passwordEncoder); // 사용자 비밀번호를 암호화하기 위한 Spring Security의 비밀번호 인코딩
        userRepository.save(user); // 새 사용자를 DB에 저장
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
    public boolean emailCheck(UserDto userDto) {
        Optional<UserEntity> userOptional = userRepository.findByEmail(userDto.getEmail());
        if (userOptional.isPresent()) {
            // 이메일이 데이터베이스에 존재하는 경우
            UserEntity user = userOptional.get();
            return true;
        } else {
            return false;
        }
    }
    
    // 핸드폰 번호 유효성 검사
    public boolean phoneCheck(UserDto userDto) {
        Optional<UserEntity> userOptional = userRepository.findByPhoneNumber(userDto.getPhoneNumber());
        if(userOptional.isPresent()){
            UserEntity user = userOptional.get();
            return true;
        }else{
            return false;
        }
    }
    
    //문자 인증번호 보내기
    public String sendSms(@RequestBody UserDto dto) {
        String authnum = generateRandomNumber(6);
        Message message = new Message();
        message.setFrom("01076331961");
        message.setTo(dto.getPhoneNumber());
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
}
