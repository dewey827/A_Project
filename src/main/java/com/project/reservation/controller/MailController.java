package com.project.reservation.controller;

import com.project.reservation.service.MailService;
import com.project.reservation.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/email")
public class MailController {

    private final MailService mailService;
    private final MemberService memberService;

    // 전송
    @PostMapping("/send")
    //  클라이언트가 전송한 이메일 주소를 추출해서 파라미터에 바인딩
    public ResponseEntity<?> send(@RequestParam("receiver") String receiver) {
        try {
            mailService.sendMail(receiver);
            // 성공시
            return ResponseEntity.ok("인증 메일이 전송되었습니다!");
        } catch (Exception e) {
            // 실패시
            return ResponseEntity.badRequest().body("메일 전송 실패");
        }
    }

    // 검증
    @PostMapping("/verify")
    //  클라이언트가 전송한 이메일 주소와 인증코드를 추출해서 파라미터에 바인딩
    public ResponseEntity<?> verify(@RequestParam("receiver") String receiver, @RequestParam("code") String code) {
        boolean isVerified = mailService.verifyCode(receiver, code);
        if (isVerified) {
            // 성공시 verificationStatus 동시성 맵에 수신자와 true 값 저장
            memberService.setEmailVerified(receiver);
            return ResponseEntity.ok("메일 인증 성공!");
        } else {
            // 실패시
            return ResponseEntity.badRequest().body("메일 인증 실패 또는 만료된 코드입니다. 메일 인증을 다시 시도해주세요.");
        }
    }
}