package com.example.wonderwoman.mail.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.Random;


@Slf4j
@Service
@RequiredArgsConstructor
@Getter
public class SignupEmailService {

    private final JavaMailSender emailsender;

    private final String ePw = createKey(); //인증번호

    @Value("${spring.mail.username}")
    private String id;

    //인증 코드 만들기
    public static String createKey() {
        StringBuffer key = new StringBuffer();
        Random random = new Random();

        for (int i = 0; i < 8; i++) {
            int idx = random.nextInt(3);

            switch (idx) {
                case 0:
                    key.append((char) ((int) random.nextInt(26) + 97));
                    break;
                case 1:
                    key.append((char) ((int) random.nextInt(26) + 65));
                    break;
                case 2:
                    key.append(random.nextInt(10));
                    break;
            }
        }
        return key.toString();
    }

    public MimeMessage createMessage(String to) throws MessagingException, UnsupportedEncodingException {
        log.info("보내는 대상 : " + to);
        log.info("인증 번호 : " + ePw);

        MimeMessage message = emailsender.createMimeMessage();

        message.addRecipients(MimeMessage.RecipientType.TO, to);    //수신자
        message.setSubject("Wonderwoman 이메일 인증 코드입니다.");   //메일 제목

        String msg = "";
        msg += "<div style='width: 100vw; height: 100vh; display: flex; flex-direction: column; align-items: center; justify-content: center;'>";
        msg += "<h1 style=\"margin-top: 10vh; font-size: 32px; font-weight: 600; width: 80vw; text-align: center;\">WonderWoman 회원가입을 위한 인증코드입니다.</h1>";
        msg += "<p style=\"margin-top: 2vh; font-size: 18px; font-weight: 400; width: 80vw; text-align: center;\">안녕하세요, 원더우먼입니다. <br /> 회원가입 페이지로 돌아가 아래 인증코드를 입력해주세요.</p>";
        msg += "<div style=\"padding: 2vh; margin-top: 4vh; font-size: 1.5rem; font-weight: 600; background-color: lightgray; border-radius: 10px; display: flex;\">";
        msg += ePw;
        msg += "</div> </div> </body> </html>";

        message.setText(msg, "utf-8", "html");
        message.setFrom(new InternetAddress(id, "wonderwoman_Admin"));

        return message;
    }

    //메일 발송
    public String sendSimpleMessage(String to) throws Exception {
        MimeMessage message = createMessage(to);

        try {
            emailsender.send(message);
        } catch (MailException es) {
            es.printStackTrace();
            throw new IllegalAccessException();
        }
        return ePw;
    }
}
