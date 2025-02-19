package com.project.reservation.service;

import com.project.reservation.common.exception.MemberException;
import com.project.reservation.dto.request.member.ReqMemberLogin;
import com.project.reservation.dto.request.member.ReqMemberRegister;
import com.project.reservation.dto.response.member.ResMember;
import com.project.reservation.entity.Member;
import com.project.reservation.repository.MemberRepository;
import com.project.reservation.security.jwt.CustomUserDetailsService;
import com.project.reservation.security.jwt.JwtTokenUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class MemberService {

    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService customUserDetailsService;
    private final JwtTokenUtil jwtTokenUtil;

    // 이메일 중복체크 - private 메소드 실행 후 성공시 HttpStatus 반환
    public HttpStatus checkIdDuplicate(String email) {
        isExistUserEmail(email);
        return HttpStatus.OK;
    }

    // 닉네임 중복체크  - private 메소드 실행 후 성공시 HttpStatus 반환
    public HttpStatus checkNickNameDuplicate(String nickName) {
        isExistUserNickName(nickName);
        return HttpStatus.OK;
    }

    // 회원가입
    public ResMember register(ReqMemberRegister reqMemberRegister) {
        isExistUserEmail(reqMemberRegister.getEmail());
        isExistUserNickName(reqMemberRegister.getNickName());
        // 비밀번호는 HttpStatus 반환 안함?
        checkPassword(reqMemberRegister.getPassword(), reqMemberRegister.getPasswordCheck());
        // 패스워드 암호화
        String encodedPassword = passwordEncoder.encode(reqMemberRegister.getPassword());
        // reqMemberRegister 에 암호화된 패스워드로 set
        reqMemberRegister.setPassword(encodedPassword);

        Member registerMember = memberRepository.save(
                ReqMemberRegister.ofEntity(reqMemberRegister));

        return ResMember.fromEntity(registerMember);
    }
    
    // 로그인
    public ResMember login(ReqMemberLogin memberLoginDto) {
        authenticate(loginDto.getEmail(), loginDto.getPassword());
        UserDetails userDetails = userDetailsService.loadUserByUsername(loginDto.getEmail());
        checkEncodePassword(loginDto.getPassword(), userDetails.getPassword());
        String token = jwtTokenUtil.generateToken(userDetails);
        return MemberTokenDto.fromEntity(userDetails, token);

        Member member = memberRepository.findByEmail(memberLoginDto.getEmail())
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        if (!new BCryptPasswordEncoder().matches(memberLoginDto.getPassword(), member.getPassword())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }
        return jwtTokenUtil.generateToken(member.getEmail());
        return ResMember.fromEntity(member);
    }

    public UserDetails loadUserByUsername(String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        return new org.springframework.security.core.userdetails.User(member.getEmail(), member.getPassword(), new ArrayList<>());
    }



    // private 메소드들 =========================================================================
    // 이메일 중복체크 - 리파지토리 조회 후 중복시 예외로 HttpStatus
    private void isExistUserEmail(String email) {
        if (memberRepository.findByEmail(email).isPresent()) {
            throw new MemberException("이미 사용 중인 이메일입니다.", HttpStatus.BAD_REQUEST);
        }
    }

    // 닉네임 중복체크 - 리파지토리 조회 후 중복시 예외만 처리
    private void isExistUserNickName(String nickName) {
        if (memberRepository.findByNickName(nickName).isPresent()) {
            throw new MemberException("이미 사용 중인 닉네임입니다.", HttpStatus.BAD_REQUEST);
        }
    }

    // 비밀번호와 비밀번호 확인 같은지 체크
    private void checkPassword(String password, String passwordCheck) {
        if (!password.equals(passwordCheck)) {
            throw new MemberException("비밀번호 확인 불일치", HttpStatus.BAD_REQUEST);
        }
    }

    // 사용자가 입력한 비밀번호가 DB 에 저장된 비밀번호와 같은지 체크
    private void checkEncodePassword(String rawPassword, String encodedPassword) {
        if (!passwordEncoder.matches(rawPassword, encodedPassword)) {
            throw new MemberException("패스워드 불일치", HttpStatus.BAD_REQUEST);
        }
    }

    // 사용자 인증
    private void authenticate(String email, String pwd) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, pwd));
        } catch (DisabledException e) {
            throw new MemberException("인증되지 않은 아이디입니다.", HttpStatus.BAD_REQUEST);
        } catch (BadCredentialsException e) {
            throw new MemberException("비밀번호가 일치하지 않습니다.", HttpStatus.BAD_REQUEST);
        }
    }


}