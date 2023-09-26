package data.service;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;


import javax.transaction.Transactional;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MailSendService {
    private int authNumber;

    private final JavaMailSender javaMailSender;
    public int makeRandomNumber() {
        Random random = new Random();
        int checkNum = random.nextInt(888888) + 111111;
//        log.info("checkNum : " + checkNum);
        authNumber = checkNum;
        return checkNum;
    }
    public String mailSend(String email) {
        int randomNumber = makeRandomNumber(); // 이미 생성된 번호를 가져옴
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setTo(email);
        simpleMailMessage.setSubject("회원가입 인증해주세요");
        simpleMailMessage.setText(String.valueOf(randomNumber));
        javaMailSender.send(simpleMailMessage);
        return String.valueOf(randomNumber);
    }

}
