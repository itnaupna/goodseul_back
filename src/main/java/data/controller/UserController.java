package data.controller;

import data.dto.GoodseulDto;
import data.dto.SignUpDto;
import data.dto.UserDto;
import data.entity.GoodseulEntity;
import data.entity.UserEntity;
import data.service.MailSendService;
import data.service.UserService;
import jwt.setting.settings.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;



@Slf4j
@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserController {

    private final UserService userService;
    private final MailSendService mailSendService;
    private final JwtService jwtService;

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
    public ResponseEntity<?> userListPaging(@RequestParam(value = "page", required = false) Integer page) {
        if (page != null) {
            // 페이징 처리
            Page<UserEntity> users = userService.userPaging(page);
            List<UserDto> pagedUserList = users.getContent().stream()
                    .map(UserDto::toUserDto)
                    .collect(Collectors.toList());
            return new ResponseEntity<>(pagedUserList, HttpStatus.OK);
        } else {
            // 전체 목록 반환
            List<UserDto> users = userService.userList();
            return new ResponseEntity<>(users, HttpStatus.OK);
        }
    }

    //구슬님 지역별 조회
    @GetMapping("/lv0/gslocation")
    public ResponseEntity<?> getGoodseulIdxByLocation(@RequestParam(value = "page", defaultValue = "0") Integer page, @RequestParam String location) {
            Page<GoodseulDto> gsPage = userService.goodseulPaging(location, page);
            List<GoodseulDto> pageGsList = gsPage.getContent();

            return new ResponseEntity<>(pageGsList, HttpStatus.OK);
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

    @PostMapping("/lv0/logout")
    public ResponseEntity<Void> logout(@RequestHeader("Authorization") String authorizationHeader) {
        // 이 부분은 accessToken에서 email을 추출하는 것이므로,
        // 실제 시나리오에 따라 필요한 정보를 header나 body에서 얻을 수 있도록 조정해야 합니다.
        String accessToken = authorizationHeader.replace("Bearer ", "");
        Long idx = jwtService.extractIdx(accessToken).orElseThrow(() -> new RuntimeException("idx 추출 실패"));

        jwtService.logout(idx);

        return ResponseEntity.ok().build();
    }

    //회원삭제
    @DeleteMapping("/lv0/deleteuser/{idx}")
    public String deleteUser(@PathVariable Long idx){
        userService.deleteUser(idx);
        return "회원삭제";
    }

    //회원 업데이트
    @PutMapping("/lv0/updateuser/{idx}")
    public String updateUser(@PathVariable Long idx, @RequestBody UserDto userdto){
        userService.updateUser(idx, userdto);
        return "회원 업데이트 완료";
    }

    @PutMapping("/lv0/updategs/{idx}")
    public String updateGoodseul(@PathVariable Long idx, @RequestBody GoodseulDto goodseulDto){
        userService.updateGoodseul(idx, goodseulDto);
        return "구슬회원 업데이트 완료";
    }
}
