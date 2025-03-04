package com.project.reservation.controller;

import com.project.reservation.entity.DeletedMember;
import com.project.reservation.dto.request.member.*;
import com.project.reservation.dto.response.member.ResMember;
import com.project.reservation.dto.response.member.ResMemberToken;
import com.project.reservation.entity.Member;
import com.project.reservation.service.MailService;
import com.project.reservation.service.MemberService;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class MemberController {

    private final MemberService memberService;
    private final MailService mailService;

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
    }
    //====================================================================================================
    // 수정
    @PatchMapping("/{memberId}/update")
    public ResponseEntity<ResMember> update(
            @AuthenticationPrincipal Member member,
            @RequestBody ReqMemberUpdate reqMemberUpdate){
        ResMember updatedMember = memberService.update(member, reqMemberUpdate);
        return ResponseEntity.ok(updatedMember);
    }

    // 삭제
    @DeleteMapping("/{memberId}/delete")
    public ResponseEntity<Long> delete(
            @PathVariable("memberId") Long memberId,
            @AuthenticationPrincipal Member member) {
        memberService.deleteMember(memberId, member);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    // !! 관리자용 !!
    @GetMapping("/deletedMember")
    public ResponseEntity<Page<DeletedMember>> deletedMemberList(
            @PageableDefault(size = 8, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<DeletedMember> deletedMembers = memberService.getAllDeletedMember(pageable);
        return ResponseEntity.status(HttpStatus.OK).body(deletedMembers);
    }

    //====================================================================================================
    // 이메일 찾기
    @PostMapping("/findId")
    public ResponseEntity<String> findId(
            @RequestBody ReqMemberFindId reqMemberFindId){
        String memberEmail = memberService.findEmail(reqMemberFindId.getName(), reqMemberFindId.getPhone());

        return ResponseEntity.ok("귀하의 이메일 입니다. : " + memberEmail);
    }
    // 비밀번호 찾기
    @PostMapping("/findPw")
    public ResponseEntity<?> findPassword(@RequestBody ReqMemberFindPw reqMemberFindPw) {

        if (memberService.isEmailExist(reqMemberFindPw.getEmail())) {

            mailService.sendMail(reqMemberFindPw.getEmail());
            return ResponseEntity.ok("비밀번호 재설정 인증 메일이 전송되었습니다.");
        } else {

            return ResponseEntity.badRequest().body("등록되지 않은 이메일입니다.");
        }
    }

    @PostMapping("/findPw/verify")
    public ResponseEntity<?> verifyCode(@RequestBody ReqMemberFindPw reqMemberFindPw) {

        if (mailService.verifyCode(reqMemberFindPw.getEmail(), reqMemberFindPw.getCode())) {

            return ResponseEntity.ok("인증 코드가 확인되었습니다.");
        } else {
            return ResponseEntity.badRequest().body("잘못된 인증 코드 혹은 만료된 인증 코드입니다.");
        }
    }

//    @PostMapping("/findPw/checkPassword") - 프론트에서
//    public ResponseEntity<?> checkPassword(@RequestBody ReqMemberFindPw reqMemberFindPw) {
//        try {
//            memberService.checkPassword(reqMemberFindPw.getNewPassword(), reqMemberFindPw.getNewPasswordCheck());
//            return ResponseEntity.ok("비밀번호가 일치합니다.");
//        } catch (MemberException e) {
//            return ResponseEntity.badRequest().build();
//        }
//    }

    @PostMapping("/findPw/resetPw")
    public ResponseEntity<?> resetPassword(@RequestBody ReqMemberFindPw reqMemberFindPw) {
        ResMember resMember = memberService.resetMemberPassword(reqMemberFindPw);
        return ResponseEntity.ok().body("비밀번호가 성공적으로 재설정되었습니다.");
    }
    //====================================================================================================
    // 마이페이지 - 올바른 비밀번호 POST 시 로그인한 사용자 정보 반환
    @GetMapping("/myPage")
    public ResponseEntity<ResMember> myPagePasswordCheck(
            @AuthenticationPrincipal Member member,
            @RequestBody ReqMemberMyPage reqMemberMyPage) {
        ResMember resMember = memberService.myPageCheck(member, reqMemberMyPage.getPassword());

        return ResponseEntity.ok(resMember);
    }
}