package com.project.reservation.controller;

import com.project.reservation.dto.request.member.ReqMemberLogin;
import com.project.reservation.dto.request.member.ReqMemberRegister;
import com.project.reservation.dto.response.member.ResMember;
import com.project.reservation.dto.response.member.ResMemberToken;
import com.project.reservation.service.MemberService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class MemberController {

    private final MemberService memberService;

    // 이메일 중복확인
    @GetMapping("/checkEmail")
    public ResponseEntity<?> checkIdDuplicate(@RequestParam(name = "email") String email) {
        memberService.checkIdDuplicate(email);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    // 닉네임 중복확인
    @GetMapping("/checkNickName")
    public ResponseEntity<?> checkNickNameDuplicate(@RequestParam(name = "nickName") String nickName) {
        memberService.checkNickNameDuplicate(nickName);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    // 이메일 인증 확인
    @GetMapping("/checkEmailVerified")
    public ResponseEntity<?> checkEmailVerified(@RequestParam(name = "emailVerified") String emailVerified) {
        memberService.checkEmailVerified(emailVerified);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    // 회원가입
    @PostMapping("/register")
    public ResponseEntity<ResMember> register(@RequestBody ReqMemberRegister reqMemberRegister) {
        ResMember registeredMember = memberService.register(reqMemberRegister);
        return ResponseEntity.ok(registeredMember);
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<ResMemberToken> login(@RequestBody ReqMemberLogin reqMemberLogin){
        // 서비스레이어에서 요청 DTO 로 로그인 메소드를 한 결과를 ResMemberToken 로 받음. 성공시 생성된 토큰 정보
        ResMemberToken resMemberToken = memberService.login(reqMemberLogin);
        return  ResponseEntity.status(HttpStatus.OK).header(resMemberToken.getToken()).body(resMemberToken);
//        return ResponseEntity.ok()
//                .header("Authorization", "Bearer " + resMemberToken.getToken())
//                .body(resMemberToken);
    }

    /** 수정 삭제 등 추후에 **/


}