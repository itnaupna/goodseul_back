package data.controller;

import com.fasterxml.jackson.databind.JsonNode;
import data.dto.*;
import data.entity.UserEntity;
import data.service.MailSendService;
import data.service.OnlineUserService;
import data.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import jwt.setting.settings.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;



@Slf4j
@CrossOrigin
@RestController
@RequiredArgsConstructor
@Api(tags = "유저 API")
@RequestMapping("/api")
public class UserController {

    private final UserService userService;
    private final MailSendService mailSendService;
    private final JwtService jwtService;
    private  final OnlineUserService onlineUserService;

    @ResponseBody
    //일반 유저 회원가입
    @PostMapping("/lv0/user/sign-up")
    @ApiOperation(value = "회원가입 API", notes = "사용자 정보를 입력하여 회원가입을 합니다.")
    public String userSignUp(@ApiParam(value = "사용자 회원가입 정보", required = true) @RequestBody UserDto userDto) throws Exception {
        userService.signUp(userDto);
        return "회원가입 성공";
    }

    //구슬님 회원가입
    @PostMapping(value = "/lv0/goodseul/sign-up", consumes = "multipart/form-data")
    @ApiOperation(value = "구슬님 회원가입 API", notes = "구슬님의 회원 정보와 관련 파일들을 통해 회원가입을 합니다.")
    public String goodseulSignUp(
            @ApiParam(value = "이메일 주소", required = true) String email,
            @ApiParam(value = "이름", required = true) String name,
            @ApiParam(value = "닉네임", required = true) String nickname,
            @ApiParam(value = "비밀번호", required = true) String password,
            @ApiParam(value = "전화번호", required = true) String phoneNumber,
            @ApiParam(value = "위치", required = true) String location,
            @ApiParam(value = "생년월일", required = true) String birth,
            @ApiParam(value = "구슬님 이름", required = true) String goodseulName,
            @ApiParam(value = "스킬", required = true) String skill,
            @ApiParam(value = "업로드 할 파일들", required = true) List<MultipartFile> uploads
    )throws Exception{
        UserDto userDto = new UserDto();
        GoodseulDto goodseulDto = new GoodseulDto();
        userDto.setEmail(email);
        userDto.setName(name);
        userDto.setNickname(nickname);
        userDto.setPassword(password);
        userDto.setPhoneNumber(phoneNumber);
        userDto.setLocation(location);
        userDto.setBirth(birth);
        goodseulDto.setGoodseulName(goodseulName);
        goodseulDto.setSkill(skill);

        userService.goodseulSignup(goodseulDto,userDto, uploads);
        return "회원가입 성공";
    }

    //유저 리스트
    @GetMapping("/lv0/userlist")
    @ApiOperation(value = "유저 리스트 조회 API", notes = "페이징 처리가 가능한 유저 리스트를 반환합니다. page 파라미터가 없을 경우 전체 목록을 반환합니다.")
    public ResponseEntity<?> userListPaging(
            @ApiParam(value = "페이지 번호 (옵션)", required = false) @RequestParam(value = "page", required = false) Integer page) {

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
    //구슬님 지역별 조회
    @GetMapping("/lv0/gslocation")
    @ApiOperation(value = "구슬님 지역별 조회 API", notes = "지정된 지역의 구슬님 목록을 페이징 처리하여 반환합니다.")
    public ResponseEntity<?> getGoodseulIdxByLocation(
            @ApiParam(value = "페이지 번호 (기본값: 0)", defaultValue = "0") @RequestParam(value = "page", defaultValue = "0") Integer page,
            @ApiParam(value = "조회할 지역 (기본값: 서울)", defaultValue = "서울") @RequestParam(value = "location", defaultValue = "서울") String location) {

        Page<GoodseulDto> gsPage = userService.goodseulPaging(location, page);
            List<GoodseulDto> pageGsList = gsPage.getContent();

            return new ResponseEntity<>(pageGsList, HttpStatus.OK);
        }
    @GetMapping("/lv0/gsskill")
    @ApiOperation(value = "구슬님 목적별 조회 API", notes = "지정된 목적을 가진 구슬님 목록을 페이징 처리하여 반환합니다.")
    public ResponseEntity<?> getGoodseulBySkill(
            @ApiParam(value = "페이지 번호 (기본값: 0)", defaultValue = "0") @RequestParam(value = "page", defaultValue = "0") Integer page,
            @ApiParam(value = "조회할 스킬", required = true) @RequestParam String skill) {

        Page<GoodseulDto> gsPage = userService.skillList(skill, page);
        List<GoodseulDto> pageGsList = gsPage.getContent();

        return new ResponseEntity<>(pageGsList, HttpStatus.OK);
    }

    // 구슬 단건 조회 API
    @GetMapping("/lv1/gs")
    @ApiOperation(value = "구슬 단건 조회 API", notes = "지정된 ID를 가진 구슬님의 정보를 조회합니다.")
    public ResponseEntity<GoodseulInfoDto> getGoodseulInfo(
            @ApiParam(value = "조회할 구슬님의 ID", required = true) @RequestParam long goodseulIdx) {

        return new ResponseEntity<GoodseulInfoDto>(userService.getGoodseulInfo(goodseulIdx), HttpStatus.OK);
    }

    // 비밀번호 변경
    @PostMapping("/lv0/pwdupdate")
    @ApiOperation(value = "비밀번호 변경 API", notes = "제공된 이메일에 해당하는 사용자의 비밀번호를 변경합니다.")
    public String pwdUpdate(
            @ApiParam(value = "비밀번호를 변경할 사용자의 이메일", required = true) @RequestParam String email,
            @ApiParam(value = "새로운 비밀번호", required = true) @RequestParam String password) {

        userService.pwdUpdate(email, password);
        return "변경";
    }
    //jwt test
    @GetMapping("/lv0/check")
    public String jwtTest(){
        return "check";
    }

    //이메일 인증번호 보내기
    @PostMapping("/lv0/mail")
    @ApiOperation(value = "이메일 전송 API", notes = "제공된 이메일 주소로 랜덤 번호를 전송하고 해당 번호를 반환합니다.")
    public int emailSend(
            @ApiParam(value = "랜덤 번호를 받을 이메일 주소", required = true) @RequestParam("email") String email) {

        int randomNumber = Integer.parseInt(mailSendService.mailSend(email)); // 이메일 보내고 랜덤 번호를 가져옴
        return randomNumber;
    }

    //이메일 유효성 검사
    @PostMapping("/lv0/emailcheck")
    @ApiOperation(value = "이메일 확인 API", notes = "제공된 이메일 주소가 유효한지 확인합니다.")
    public boolean checkEmail(
            @ApiParam(value = "확인할 이메일 정보가 포함된 Json", required = true) @RequestBody JsonNode jsonNode) {
        log.info(jsonNode.get("email").asText());
        return userService.emailCheck(jsonNode.get("email").asText());
    }


    // 닉네임 유효성 검사
    @PostMapping("/lv0/nicknamecheck")
    @ApiOperation(value = "닉네임 유효성 검사 API", notes = "제공된 닉네임이 유효한지 확인합니다.")
    public boolean checknickname(
            @ApiParam(value = "확인할 닉네임 정보가 포함된 Json", required = true) @RequestBody JsonNode jsonNode) {
        return userService.nicknameCheck(jsonNode.get("nickname").asText());
    }


    //핸드폰 번호 유효성 검사
    @PostMapping("/lv0/phonecheck")
    @ApiOperation(value = "핸드폰 번호 유효성 검사 API", notes = "제공된 핸드폰 번호가 유효한지 확인합니다.")
    public boolean checkPhone(@ApiParam(value = "확인할 핸드폰 번호 정보가 포함된 Json", required = true) @RequestBody JsonNode jsonNode){
        return userService.phoneCheck(jsonNode.get("phoneNumber").asText());
    }

    //문자 인증번호 발송
    @PostMapping("/lv0/sms")
    @ApiOperation(value = "문자 인증번호 발송 API", notes = "제공된 핸드폰 번호로 인증번호를 발송합니다.")
    public ResponseEntity<String> sendSms(@ApiParam(value = "문자 인증번호를 발송할 핸드폰 번호 정보가 포함된 Json", required = true) @RequestBody JsonNode jsonNode){
        String authnum = userService.sendSms(jsonNode.get("phoneNumber").asText());
        return ResponseEntity.ok(authnum);
    }

    // 로그아웃
    @PostMapping("/lv0/logout")
    @ApiOperation(value = "로그아웃 API", notes = "사용자 로그아웃을 처리합니다.")
    public ResponseEntity<Void> logout(@ApiParam(value = "Authorization 헤더 값", required = true) @RequestHeader("Authorization") String authorizationHeader) {
        // 이 부분은 accessToken에서 email을 추출하는 것이므로,
        // 실제 시나리오에 따라 필요한 정보를 header나 body에서 얻을 수 있도록 조정해야 합니다.
        String accessToken = authorizationHeader.replace("Bearer ", "");
        Long idx = jwtService.extractIdx(accessToken).orElseThrow(() -> new RuntimeException("idx 추출 실패"));

        jwtService.logout(idx);
        onlineUserService.removeUser(idx);

        return ResponseEntity.ok().build();
    }

    //3가지 유효성 검사
    @PostMapping("/lv0/check")
    @ApiOperation(value = "3가지 유효성 검사 API", notes = "제공된 이메일, 생년월일, 이름에 대한 유효성 검사를 합니다.")
    public ResponseEntity<String> allCheck(@ApiParam(value = "이메일, 생년월일, 이름 정보", required = true) @RequestBody UserCheckDto userCheckDto) {
        String phoneNumber = userService.allCheck(userCheckDto.getEmail(), userCheckDto.getBirth(), userCheckDto.getName());
        if (phoneNumber != null) {
            return ResponseEntity.ok(phoneNumber);
        }
        return ResponseEntity.badRequest().body("Check failed");
    }


    //회원 업데이트
    @PatchMapping ("/lv1/user")
    @ApiOperation(value = "회원 정보 업데이트 API", notes = "사용자의 회원 정보를 업데이트합니다.")
    public ResponseEntity<UserDto> updateUser(
            @ApiParam(value = "HttpServletRequest object", required = true) HttpServletRequest request,
            @ApiParam(value = "업데이트할 회원 정보", required = true) @RequestBody UserDto userdto){
        userService.updateUser(request, userdto);
        return new ResponseEntity<>(userdto,HttpStatus.OK);
    }

    //구슬 업데이트
    @PatchMapping("/lv1/goodseul")
    @ApiOperation(value = "구슬 정보 업데이트 API", notes = "구슬의 정보를 업데이트합니다.")
    public ResponseEntity<GoodseulDto> updateGoodseul(
            @ApiParam(value = "HttpServletRequest object", required = true) HttpServletRequest request,
            @ApiParam(value = "업데이트할 구슬 정보", required = true) @RequestBody GoodseulDto goodseulDto){
        userService.updateGoodseul(request, goodseulDto);
        return new ResponseEntity<>(goodseulDto,HttpStatus.OK);
    }

    //사진 업로드
    @PatchMapping("/lv1/profile")
    @ApiOperation(value = "프로필 사진 업로드 API", notes = "사용자의 프로필 사진을 업로드합니다.")
    public ResponseEntity<String> updatePhoto(
            @ApiParam(value = "HttpServletRequest object", required = true) HttpServletRequest request,
            @ApiParam(value = "업로드할 사진 파일", required = true) @RequestBody MultipartFile upload) throws IOException {
        String fileName;
        try{
            fileName = userService.updatePhoto(upload,request);
        }catch (IOException e){
            throw new IOException("오류류");
        }
        return ResponseEntity.ok(fileName);
    }

    // 실시간 접속 구슬 유저
    @GetMapping("/lv0/online")
    @ApiOperation(value = "실시간 접속 구슬 유저 조회 API", notes = "현재 실시간으로 접속 중인 구슬 유저 목록을 반환합니다.")
    public ResponseEntity<List<GoodseulResponseDto>> getOnlineUsers() {
        List<GoodseulResponseDto> onlineUsers = onlineUserService.getOnlineUsers();
        return new ResponseEntity<>(onlineUsers, HttpStatus.OK);
    }

    // 아이디 찾기
    @PostMapping("/lv0/find-id")
    @ApiOperation(value = "아이디 찾기 API", notes = "이름, 핸드폰 번호, 생년월일로 회원의 아이디를 찾습니다.")
    public ResponseEntity<String> findByEmail (
            @ApiParam(value = "이름", required = true) @RequestParam String name,
            @ApiParam(value = "핸드폰 번호", required = true) @RequestParam String phone,
            @ApiParam(value = "생년월일", required = true) @RequestParam String birth) {
        try {
            String email = userService.findByEmail(name, phone, birth);
            return new ResponseEntity<>(email, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NO_CONTENT);
        }
    }

    // 회원 탈퇴
    @PutMapping("/lv1/sign-out")
    @ApiOperation(value = "회원 탈퇴 API", notes = "현재 로그인된 사용자의 회원 탈퇴를 진행합니다.")
    public ResponseEntity<String> signOut(
            @ApiParam(value = "HttpServletRequest object", required = true) HttpServletRequest request) {
        boolean result = userService.signOut(request);
        if (result) {
            return new ResponseEntity<>("회원 탈퇴 성공", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("회원 탈퇴 실패", HttpStatus.NOT_FOUND);
        }
    }

}
