package data.controller;

import data.dto.GoodseulDto;
import data.dto.SignUpDto;
import data.dto.UserDto;
import data.entity.UserEntity;
import data.service.MailSendService;
import data.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Slf4j
@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserController {

    private final UserService userService;
    private final MailSendService mailSendService;

    @ResponseBody
    //회원가입
    @PostMapping("/lv0/user/sign-up")
    public String userSignUp(@RequestBody UserDto userDto) throws Exception{
        userService.signUp(userDto);
        return "회원가입 성공";
    }
    @ResponseBody
    @PostMapping("/lv0/goodseul/sign-up")
    public String goodseulSignUp(@RequestBody SignUpDto requestDto)throws Exception{
        userService.goodseulSignup(requestDto.getGoodseulDto(),requestDto.getUserDto());
        return "회원가입 성공";
    }

    @GetMapping("/lv0/userlist")
    public Page<UserEntity> getUserList(@RequestParam(defaultValue = "0")long idx) {
        return userService.userPaging(idx);
    }
    @GetMapping("/lv0/list")
    public List<UserDto> List(){
        return userService.userList();
    }
    @GetMapping("/lv0/goodseullist")
    public List<GoodseulDto> goodseulList(){
        return userService.goodseulList();
    }
    @ResponseBody
    //비밀번호 변경
    @PostMapping("/lv0/pwdupdate")
    public String pwdUpdate(@RequestBody UserDto userdto){
        userService.pwdUpdate(userdto.getEmail(), userdto.getPassword());
        return "변경";
    }
    //jwt test
    @GetMapping("/jwt-test")
    public String jwtTest(){
        return "jwtTest 요청 성공";
    }

    //이메일 인증번호 보내기
    @ResponseBody
    @PostMapping("/lv0/mail")
    public int emailSend(@RequestParam("email") String email){
        int randomNumber = Integer.parseInt(mailSendService.mailSend(email)); // 이메일 보내고 랜덤 번호를 가져옴
        return randomNumber;
    }
    @ResponseBody
    //이메일 유효성 검사
    @PostMapping("/lv0/emailcheck")
    public ResponseEntity<String> checkEmail(@RequestBody UserDto userDto){
        boolean emailcheck = userService.emailCheck(userDto);
        if(emailcheck){
            return ResponseEntity.ok(userDto.getEmail());
        }else{
            return ResponseEntity.notFound().build(); // HTTP 400 Bad Request
        }
    }
    
    //핸드폰 번호 유효성 검사
    @ResponseBody
    @PostMapping("/lv0/phonecheck")
    public ResponseEntity<String> checkPhone(@RequestBody UserDto userDto){
        boolean phonecheck = userService.phoneCheck(userDto);
        if(phonecheck){
            return ResponseEntity.ok(userDto.getPhoneNumber());
        }else{
            return ResponseEntity.notFound().build();
        }
    }
    
    //문자 인증번호 발송
    @PostMapping("/lv0/sms")
    public String sendSms(@RequestBody UserDto userdto){
        String authnum = userService.sendSms(userdto);
        return authnum;
    }
}
