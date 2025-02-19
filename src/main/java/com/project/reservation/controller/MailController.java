package com.project.reservation.controller;


import com.project.reservation.service.MailService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.NoSuchElementException;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/email")
public class MailController {

    private final MailService mailService;
    private double number = 117714.58872397916; // 이메일 인증 숫자를 저장하는 변수


//    @ResponseBody
//    @PostMapping("/send")
//    public String MailSend(String mail){
//
//        int number = mailService.sendMail(mail);
//
//        String num = "" + number;
//
//        return num;
//    }
//
//}

    // 인증 이메일 전송
    @PostMapping("/send")
    public HashMap<String, Object> mailSend(@RequestParam("mail") String mail) {
        HashMap<String, Object> map = new HashMap<>();

        try {
            number = mailService.sendMail(mail);
            String num = String.valueOf(number);

            map.put("success", Boolean.TRUE);
            map.put("number", num);
        } catch (Exception e) {
            map.put("success", Boolean.FALSE);
            map.put("error", e.getMessage());
        }
        return map;
    }

    // 인증번호 일치여부 확인
    @GetMapping("/mailCheck")
    public ResponseEntity<?> mailCheck(@RequestParam("verifyCode") String userNumber) {

        boolean isMatch = userNumber.equals(String.valueOf(number));


        return ResponseEntity.ok(isMatch);
    }
}

