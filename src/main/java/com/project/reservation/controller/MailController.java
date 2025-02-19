package com.project.reservation.controller;

import com.project.reservation.service.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequiredArgsConstructor
@RequestMapping("/email")
public class MailController {

    private final MailService mailService;
    // 이메일 주소를 키로, 인증 코드 정보를 값으로 가지는 동시성 지원 맵
    private final Map<String, VerificationCode> verificationCodes = new ConcurrentHashMap<>();

    // 이메일 전송 요청을 처리하는 메서드
    @PostMapping("/send")
    public ResponseEntity<?> mailSend(@RequestParam("mail") String mail) {
        try {
            // 이메일 전송 및 인증 번호 생성
            int number = mailService.sendMail(mail);
            // 생성된 인증 번호를 맵에 저장
            verificationCodes.put(mail, new VerificationCode(number));
            // 성공 응답 반환
            return ResponseEntity.ok("인증 메일이 전송되었습니다.");
        } catch (Exception e) {
            // 실패 시 에러 메시지와 함께 BadRequest 응답 반환
            return ResponseEntity.badRequest().body("메일 전송 실패: " + e.getMessage());
        }
    }

    // 인증 코드 확인 요청을 처리하는 메서드
    @PostMapping("/verify")
    public ResponseEntity<?> verifyCode(@RequestParam("mail") String mail, @RequestParam("code") String code) {
        // 저장된 인증 코드 정보 조회
        VerificationCode storedCode = verificationCodes.get(mail);
        // 인증 코드가 존재하고, 유효하며, 입력된 코드와 일치하는지 확인
        if (storedCode != null && storedCode.isValid() && storedCode.getCode() == Integer.parseInt(code)) {
            // 인증 성공 시 맵에서 해당 인증 정보 제거
            verificationCodes.remove(mail);
            // 성공 응답 반환
            return ResponseEntity.ok("인증 성공");
        } else {
            // 인증 실패 시 BadRequest 응답 반환
            return ResponseEntity.badRequest().body("인증 실패 또는 만료된 코드");
        }
    }

    // 인증 코드 정보를 저장하는 내부 클래스
    private static class VerificationCode {
        private final int code;  // 인증 코드
        private final long expirationTime;  // 만료 시간

        public VerificationCode(int code) {
            this.code = code;
            // 현재 시간으로부터 5분 후를 만료 시간으로 설정
            this.expirationTime = System.currentTimeMillis() + 300000; // 5분 유효
        }

        // 인증 코드 반환 메서드
        public int getCode() {
            return code;
        }

        // 인증 코드의 유효성 검사 메서드
        public boolean isValid() {
            // 현재 시간이 만료 시간보다 이전인지 확인
            return System.currentTimeMillis() < expirationTime;
        }
    }
}
