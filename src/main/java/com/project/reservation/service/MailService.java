package com.project.reservation.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailService {
    // 메일링을 위한 JavaMailSender 인터페이스
    private final JavaMailSender javaMailSender;
    private static final String senderEmail= "dldudgus827@gmail.com";
    private static int number;

    // createNumber - 6자리 인증번호 랜덤으로 생성 메소드
    public static void createNumber() {
        number = (int)(Math.random() * 900000) + 100000; //(int) Math.random() * (최댓값-최소값+1) + 최소값
    }


    public MimeMessage CreateMail(String mail) {
        // 위에서 만든 createNumber 메소드 사용
        createNumber();
        MimeMessage message = javaMailSender.createMimeMessage();

        try {
            message.setFrom(senderEmail);
            message.setRecipients(MimeMessage.RecipientType.TO, mail);
            message.setSubject("이메일 인증");
            String body = "";
            body += "<h3>" + "요청하신 인증 번호입니다." + "</h3>";
            body += "<h1>" + number + "</h1>";
            body += "<h3>" + "감사합니다." + "</h3>";
            message.setText(body,"UTF-8", "html");
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return message;
    }

    public int sendMail(String mail) {
        MimeMessage message = CreateMail(mail);
        javaMailSender.send(message);

        return number;
    }

}