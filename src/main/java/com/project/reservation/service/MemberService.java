package com.project.reservation.service;

import com.project.reservation.common.exception.MemberException;
import com.project.reservation.dto.request.member.ReqMemberLogin;
import com.project.reservation.dto.request.member.ReqMemberRegister;
import com.project.reservation.dto.response.member.ResMember;
import com.project.reservation.dto.response.member.ResMemberToken;
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
    public ResMemberToken login(ReqMemberLogin reqMemberLogin) {
        // authenticate 메소드에 (로그인 요청 DTO 의 email, 로그인 요청 DTO 의 password)
        authenticate(reqMemberLogin.getEmail(), reqMemberLogin.getPassword());
        // customUserDetailsService 에서 반환되는 UserDetails 객체를 foundMember 이름으로 대입
        UserDetails foundMember = customUserDetailsService.loadUserByUsername(reqMemberLogin.getEmail());
        // checkEncodePassword 메소드로 DB 에 저장된 암호화된 비밀번호와 같은지 체크
        checkStoredPasswordInDB(reqMemberLogin.getPassword(), foundMember.getPassword());
        String token = jwtTokenUtil.generateToken(foundMember);
        return ResMemberToken.fromEntity(foundMember, token);
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

    // 사용자가 입력한 비밀번호가 DB 에 저장된 암호화된 비밀번호와 같은지 체크
    private void checkStoredPasswordInDB(String typedPassword, String encodedPassword) {
        if (!passwordEncoder.matches(typedPassword, encodedPassword)) {
            throw new MemberException("패스워드 불일치", HttpStatus.BAD_REQUEST);
        }
    }

    // 사용자 인증
    private void authenticate(String email, String password) {
        try {
            // 주입받은 authenticationManager 객체로 authenticate 메소드를 호출해 인증 시도
            // 이메일과 비밀번호를 받은 UsernamePasswordAuthenticationToken 객체를 생성
            // AuthenticationManager 에 전달 ?
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
            // isEnabled
        } catch (DisabledException e) {
            throw new MemberException("계정이 비활성화 되었습니다.", HttpStatus.BAD_REQUEST);
            // 비밀번호가 일치하지 않음
        } catch (BadCredentialsException e) {
            throw new MemberException("비밀번호가 일치하지 않습니다.", HttpStatus.BAD_REQUEST);
        }
    }

}