package com.project.reservation.controller;

import com.project.reservation.dto.request.member.ReqMemberLogin;
import com.project.reservation.dto.request.member.ReqMemberRegister;
import com.project.reservation.dto.response.member.ResMember;
import com.project.reservation.service.MailService;
import com.project.reservation.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;

import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/register")
    public ResponseEntity<ResMember> register(@RequestBody ReqMemberRegister reqMemberRegister) {
        ResMember resMember = memberService.register(reqMemberRegister);
        return ResponseEntity.ok(resMember);
    }

    @PostMapping("/login")
    public ResponseEntity<ResMember> login(@RequestBody ReqMemberLogin reqMemberLogin){
        ResMember resMember = memberService.login(reqMemberLogin);
        return  ResponseEntity.ok(resMember);
    }
}