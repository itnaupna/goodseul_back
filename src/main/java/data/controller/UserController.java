package data.controller;

import com.fasterxml.jackson.databind.JsonNode;
import data.dto.GoodseulDto;
import data.dto.SignUpDto;
import data.dto.UserDto;
import data.entity.UserEntity;
import data.service.MailSendService;
import data.service.UserService;
import data.service.file.StorageService;
import jwt.setting.settings.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
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
    //일반 유저 회원가입
    @PostMapping("/lv0/user/sign-up")
    public String userSignUp(@RequestBody UserDto userDto) throws Exception{
        userService.signUp(userDto);
        return "회원가입 성공";
    }

    //구슬님 회원가입
    @PostMapping(value = "/lv0/goodseul/sign-up", consumes = "multipart/form-data")
    public String goodseulSignUp( UserDto userDto, GoodseulDto goodseulDto, MultipartFile upload)throws Exception{
        userService.goodseulSignup(goodseulDto,userDto, upload);
        return "회원가입 성공";
    }

    //유저 리스트
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
    @GetMapping("/lv0/gsskill")
    public ResponseEntity<?> getGoodseulBySkill(@RequestParam(value = "page", defaultValue = "0") Integer page, @RequestParam String skill){
        Page<GoodseulDto> gsPage = userService.skillList(skill, page);
        List<GoodseulDto> pageGsList = gsPage.getContent();

        return new ResponseEntity<>(pageGsList, HttpStatus.OK);
    }
    @ResponseBody
    //비밀번호 변경
    @PostMapping("/lv0/pwdupdate")
    public String pwdUpdate(@RequestBody UserDto userdto){
        userService.pwdUpdate(userdto.getEmail(), userdto.getPassword());
        return "변경";
    }
    //jwt test
    @GetMapping("/lv0/jwt-test")
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
    //이메일 유효성 검사
    @PostMapping("/lv0/emailcheck")
    public boolean checkEmail(@RequestBody JsonNode jsonNode){
        log.info(jsonNode.get("email").asText());
        return userService.emailCheck(jsonNode.get("email").asText());
        }


    //닉네임 유효성 검사
    @PostMapping("/lv0/nicknamecheck")
    public boolean checknickname(@RequestBody JsonNode jsonNode){
        return userService.nicknameCheck(jsonNode.get("nickname").asText());
    }

    //핸드폰 번호 유효성 검사
    @PostMapping("/lv0/phonecheck")
    public boolean checkPhone(@RequestBody JsonNode jsonNode){
        return userService.phoneCheck(jsonNode.get("phoneNumber").asText());
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

//    //회원삭제
//    @DeleteMapping("/lv0/deleteuser/{idx}")
//    public String deleteUser(@PathVariable Long idx){
//        userService.deleteUser(idx);
//        return "회원삭제";
//    }

    //회원 업데이트
    @PatchMapping ("/lv1/user")
    public ResponseEntity<UserDto> updateUser(HttpServletRequest request, @RequestBody UserDto userdto){
        userService.updateUser(request, userdto);
        return new ResponseEntity<>(userdto,HttpStatus.OK);
    }
    
    //구슬 업데이트
    @PatchMapping("/lv1/goodseul")
    public ResponseEntity<GoodseulDto> updateGoodseul(HttpServletRequest request, @RequestBody GoodseulDto goodseulDto){
        userService.updateGoodseul(request, goodseulDto);
        return new ResponseEntity<>(goodseulDto,HttpStatus.OK);
    }

    //사진 업로드
    @PatchMapping("/lv1/profile")
    public ResponseEntity<String> updatePhoto(HttpServletRequest request, @RequestBody MultipartFile upload) throws IOException {
        String fileName;
        try{
            fileName = userService.updatePhoto(upload,request);
        }catch (IOException e){
            throw new IOException("오류류");
        }
        return ResponseEntity.ok(fileName);
    }


}
