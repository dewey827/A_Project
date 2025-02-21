package com.project.reservation.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailService {

    // 메일링을 위한 JavaMailSender 인터페이스 주입
    private final JavaMailSender javaMailSender;
    private static final String SENDER= "dldudgus827@gmail.com";    // 동물병원 대표 이메일 주소,
    private int number;

    // (동시성 맵 1) String 이메일 주소를 키로, Integer 인증 코드를 값으로 가지는 '동시성' 맵. 동시에 여러 스레드가 맵에 접근해 정보를 교환해도 안전하다.
    private final Map<String, Integer> verificationCodes = new ConcurrentHashMap<>();
    // (동시성 맵 2) String 이메일 주소를 키로, Integer 인증 코드를 값으로 가지는 '동시성' 맵. 동시에 여러 스레드가 맵에 접근해 정보를 교환해도 안전하다.
    private final Map<String, Long> expirationTimes = new ConcurrentHashMap<>();

    // createNumber - 6자리 인증번호를 랜덤으로 생성 메소드
    public void createNumber() {
        // 100000 ~ 999999 사이의 인증번호 생성해서 number 변수에 대입
        number = (int)(Math.random() *10 * 90000) + 100000;
    }

    // createMail- 매개변수로 수신자의 메일주소를 받아서 메일 본문을 생성하고 리턴하는 MimeMessage 타입의 메소드
    public MimeMessage createMail(String receiver) {
        // 위에서 만든 createNumber 메소드 사용해서 인증번호로 쓸 number 생성
        createNumber();
        // JavaMailSender - MimeMessage 객체를 생성하고 조작 가능. 생성된 객체는 MimeMessage 클래스(HTML 형식의 이메일) 의 인스턴스
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();

        try {
            // 발신자 설정
            mimeMessage.setFrom(SENDER);
            // 수신자 설정. setRecipients 메소드로 MimeMessage.RecipientType.TO 로 주 수신자를 설정하겠다, 받을 상대의 메일 주소
            mimeMessage.setRecipients(MimeMessage.RecipientType.TO, receiver);
            // 메일 제목 설정
            mimeMessage.setSubject("Hi Pet 동물병원 - 이메일 인증번호 입니다.");

            // 빈 문자열을 가진 변수 선언
            String body = "";
            // 새로운 문자열을 추가하여 다시 변수에 대입
            body += "<h3>" + "요청하신 인증 번호입니다. 아래의 인증번호를 인증번호 입력창에 입력하세요." + "</h3>";
            body += "<h1>" + number + "</h1>";
            body += "<h3>" + "감사합니다." + "</h3>";
            body += "<h5>" + "인증번호는 발송 요청 후 5분 동안 사용 하실 수 있습니다. 만료 후에는 새로운 인증번호를 발급받아 주세요." + "</h5>";

            // MimeMessage 에서 생성한 인스턴스에 만들어진 메일 본문, UTF-8, html 설정
            mimeMessage.setText(body,"UTF-8", "html");
        // MessagingException - JavaMail API 의 예외 클래스.
        } catch (MessagingException e) {
            log.error("메일 생성 실패: " + e.getMessage());
        }
        // 만들어진 인스턴스 리턴
        return mimeMessage;
    }

    // sendMail - 매개변수로 수신자의 메일주소를 받아서 메일을 보내고 동시성맵에 저장하는 메소드
    public void sendMail(String receiver) {
        // createMail(수신자 메일주소) 메소드 실행해서 MimeMessage 인스턴스인 mail 변수에 할당
        MimeMessage mail = createMail(receiver);
        // send 메소드로 mail 인스턴스를 보냄.
        try{
            javaMailSender.send(mail);
            storeVerificationCode(receiver, number);
        } catch (MailException e){
            log.error("메일 전송 실패: " + e.getMessage());
        }
    }

    // storeVerificationCode - 동시성 맵에 저장 메서드
    private void storeVerificationCode(String receiver, int code) {
        // (동시성 맵 1) 에 수신자와 인증코드 저장
        verificationCodes.put(receiver, code);
        // (동시성 맵 2) 에 수신자와 만료시간 저장. 5분 후 만료
        expirationTimes.put(receiver, System.currentTimeMillis() + 300000);
    }

    // verifyCode - 동시성 맵에서 검증 메서드
    public boolean verifyCode(String receiver, String code) {
        // (동시성 맵 1) 에서 수신자를 키로 가져온 인증코드 storedCode 에 대입
        Integer storedCode = verificationCodes.get(receiver);
        // (동시성 맵 2) 에서 수신자를 키로 가져온 만료시간 expirationTime 에 대입
        Long expirationTime = expirationTimes.get(receiver);
        // 인증코드가 null 이 아니고(컨트롤러에서 이메일 전송에 성공했다), 만료시간이 null 이 아니면(서비스에서 저장에 성공했다)
        if (storedCode != null && expirationTime != null) {
            // 저장된 인증코드와 사용자가 입력한 인증번호가 일치하고, 인증코드가 만료되지 않았다면
            if (storedCode == Integer.parseInt(code) && System.currentTimeMillis() < expirationTime) {
                // (동시성 맵 1) 에서 receiver 를 키값으로 가진 데이터 삭제
                verificationCodes.remove(receiver);
                // (동시성 맵 2) 에서 receiver 를 키값으로 가진 데이터 삭제
                expirationTimes.remove(receiver);
                return true;
            }
        }
        log.info("메일 검증 실패");
        return false;
    }

    public boolean verifyCodeForFindPw(String receiver, String code) {
        // (동시성 맵 1) 에서 수신자를 키로 가져온 인증코드 storedCode 에 대입
        Integer storedCode = verificationCodes.get(receiver);
        // (동시성 맵 2) 에서 수신자를 키로 가져온 만료시간 expirationTime 에 대입
        Long expirationTime = expirationTimes.get(receiver);
        // 인증코드가 null 이 아니고(컨트롤러에서 이메일 전송에 성공했다), 만료시간이 null 이 아니면(서비스에서 저장에 성공했다)
        if (storedCode != null && expirationTime != null) {
            // 저장된 인증코드와 사용자가 입력한 인증번호가 일치하고, 인증코드가 만료되지 않았다면
            if (storedCode == Integer.parseInt(code) && System.currentTimeMillis() < expirationTime) {

                return true;
            }
        }
        // (동시성 맵 1) 에서 receiver 를 키값으로 가진 데이터 삭제
        verificationCodes.remove(receiver);
        // (동시성 맵 2) 에서 receiver 를 키값으로 가진 데이터 삭제
        expirationTimes.remove(receiver);

        log.info("메일 검증 실패");
        return false;
    }




}