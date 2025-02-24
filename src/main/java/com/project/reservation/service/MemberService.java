package com.project.reservation.service;

import com.project.reservation.common.exception.MemberException;
import com.project.reservation.common.exception.ResourceNotFoundException;
import com.project.reservation.controller.MailController;
import com.project.reservation.dto.request.member.ReqMemberLogin;
import com.project.reservation.dto.request.member.ReqMemberRegister;
import com.project.reservation.dto.request.member.ReqMemberUpdate;
import com.project.reservation.dto.request.member.ReqOAuth2;
import com.project.reservation.dto.response.member.ResMember;
import com.project.reservation.dto.response.member.ResMemberToken;
import com.project.reservation.entity.Member;
import com.project.reservation.entity.Pet;
import com.project.reservation.repository.MemberRepository;
import com.project.reservation.repository.PetRepository;
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

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class MemberService {

    private final MemberRepository memberRepository;
    private final PetRepository petRepository;
    private final CustomUserDetailsService customUserDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;

    public String findEmail(String name, String phone) {
        Member member = memberRepository.findByNameAndPhone(name, phone)
                .orElseThrow(() -> new MemberException("회원정보가 존재하지 않습니다.", HttpStatus.BAD_REQUEST));
        return member.getEmail();
    }

    public boolean isEmailExist(String email) {
        return memberRepository.findByEmail(email).isPresent();
    }


    @Transactional
    public void resetPassword(String email, String newPassword) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new MemberException("회원을 찾을 수 없습니다.", HttpStatus.NOT_FOUND));

        String encodedPassword = passwordEncoder.encode(newPassword);
        member.resetPassword(encodedPassword);
    }


    //=========================================================================================================
    // 인증상태를 관리하기 위한 동시성 맵. 키값은 수신자, 값은 true/false
    private final Map<String, Boolean> verificationStatus = new ConcurrentHashMap<>();

    // 비밀번호와 비밀번호 확인 같은지 체크
    public void checkPassword(String password, String passwordCheck) {
        if (!password.equals(passwordCheck)) {
            throw new MemberException("비밀번호 확인 불일치", HttpStatus.BAD_REQUEST);
        }
    }

    // 이메일 중복체크 - private 메소드 실행 후 HttpStatus 반환
    public HttpStatus checkIdDuplicate(String email) {
        isExistUserEmail(email);
        return HttpStatus.OK;
    }

    // 닉네임 중복체크  - private 메소드 실행 후 HttpStatus 반환
    public HttpStatus checkNickNameDuplicate(String nickName) {
        isExistUserNickName(nickName);
        return HttpStatus.OK;
    }

    // checkEmailVerified
    public HttpStatus checkEmailVerified(String receiver) {
        isEmailVerified(receiver);
        return HttpStatus.OK;
    }

    // 이메일 체크 여부
    public void setEmailVerified(String receiver) {
        verificationStatus.put(receiver, true);
    }

    // 회원가입
    public ResMember register(ReqMemberRegister reqMemberRegister) {
        isExistUserEmail(reqMemberRegister.getEmail());
        isExistUserNickName(reqMemberRegister.getNickName());

        // 이메일 인증 여부 확인
        if (isEmailVerified(reqMemberRegister.getEmail())){
            // 비밀번호는 HttpStatus 반환 안함?
            checkPassword(reqMemberRegister.getPassword(), reqMemberRegister.getPasswordCheck());

            // 패스워드 암호화
            String encodedPassword = passwordEncoder.encode(reqMemberRegister.getPassword());

            // reqMemberRegister 에 암호화된 패스워드로 set
            reqMemberRegister.setPassword(encodedPassword);
        }
        // DTO 를 엔티티 객체로 변환, 변환된 Member 엔티티를 데이터베이스에 저장, 변수에 대입
        Member registerMember = memberRepository.save(
                ReqMemberRegister.ofEntity(reqMemberRegister));
        // 클라이언트에게 저장된 registerMember 엔티티를  다시 응답 DTO 로 변환해서 반환
        return ResMember.fromEntity(registerMember);
    }
    
    // 로그인
    public ResMemberToken login(ReqMemberLogin reqMemberLogin) {
        // authenticate 메소드에 (로그인 요청 DTO 의 email, 로그인 요청 DTO 의 password)
        authenticate(reqMemberLogin.getEmail(), reqMemberLogin.getPassword());
        // customUserDetailsService 에서 반환되는 UserDetails 객체를 foundMember 이름으로 대입
        UserDetails foundMember = customUserDetailsService.loadUserByUsername(reqMemberLogin.getEmail());
        // checkStoredPasswordInDB 메소드로 입력된 비밀번호가 DB 에 저장된 암호화된 비밀번호와 같은지 체크
        checkStoredPasswordInDB(reqMemberLogin.getPassword(), foundMember.getPassword());
        /** unifiedGenerateToken 로 바꾸는거 고려해보기 **/
        // foundMember 로 토큰 생성
        String token = jwtTokenUtil.generateToken(foundMember);
        // 클라이언트에게 응답으로 토큰 보냄
        return ResMemberToken.fromEntity(foundMember, token);
    }

    // 마이페이지 - 비밀번호 확인
    public ResMember myPageCheck(Member member, String typedPassword) {
        // 현재 로그인한 멤버 (Member member) 의 정보를 조회, ResMember 로 사용하기 위해 UserDetails 타입을 Member 타입으로 캐스팅
        Member currentMember = (Member) customUserDetailsService.loadUserByUsername(member.getEmail());
        // checkStoredPasswordInDB 메소드로 사용자가 입력하는 비밀번호가 DB 의 사용자의 비밀번호와 일치하는지 확인
        checkStoredPasswordInDB(typedPassword, currentMember.getPassword());
        log.info("memberservice - myPageCheck 사용됨");
        // 성공하면 조회된 사용자 정보를 DTO 로 변환하여 반환
        return ResMember.fromEntity(currentMember);
    }

    /**
     *
    // 수정
    public ResMember update(Member member, ReqMemberUpdate reqMemberUpdate) {
        // 업데이트 폼의 새 비밀번호와 비밀번호 확인이 일치하는지 확인
        checkPassword(reqMemberUpdate.getPassword(), reqMemberUpdate.getPasswordCheck());
        // 새 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(reqMemberUpdate.getPassword());
        //
        Member currentMember =  memberRepository.findByEmail(member.getEmail()).orElseThrow(
                () -> new ResourceNotFoundException("Member", "Member Email", member.getEmail())
        );
        currentMember.updateMember(encodedPassword, reqMemberUpdate.getNickName(), reqMemberUpdate.getAddr(), reqMemberUpdate.getBirth(), reqMemberUpdate.getPhone());
        return ResMember.fromEntity(currentMember);
    }

     탈퇴 등 추후에 **/



    public Member createOrUpdateOAuth2Member(ReqOAuth2 reqOAuth2) {
        return memberRepository.findByEmail(reqOAuth2.getEmail())
                .map(existingMember -> updateExistingMember(existingMember, reqOAuth2))
                .orElseGet(() -> createNewOAuth2Member(reqOAuth2));
    }

    private Member updateExistingMember(Member existingMember, ReqOAuth2 reqOAuth2) {
        existingMember.updateOAuth2Info(reqOAuth2.getProvider(), reqOAuth2.getProviderId());
        return memberRepository.save(existingMember);
    }

    private Member createNewOAuth2Member(ReqOAuth2 reqOAuth2) {
        return memberRepository.save(reqOAuth2.ofEntity(reqOAuth2));
    }


    //=================================================================================================================

// 회원의 펫 정보 조회
    public List<Pet> getPetsByMemberId(Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new RuntimeException("Member not found"));
        return member.getPets();
    }

//    // 펫 정보 수정
//    @Transactional
//    public void updatePet(Long petId, String name, String breed, int age, double weight) {
//        Pet pet = petRepository.findById(petId).orElseThrow(() -> new RuntimeException("Pet not found"));
//        pet.updatePet(name, breed, age, weight);
//        petRepository.save(pet);
//    }
//
//    // 펫 정보 삭제
//    @Transactional
//    public void deletePet(Long petId) {
//        petRepository.deleteById(petId);
//    }

    
    // private 메소드들 =========================================================================
    // 이메일 중복체크 - 리파지토리 조회 후 중복시 예외 처리
    private void isExistUserEmail(String email) {
        if (memberRepository.findByEmail(email).isPresent()) {
            throw new MemberException("이미 사용 중인 이메일입니다.", HttpStatus.BAD_REQUEST);
        }
    }

    // 이메일 인증체크
    private boolean isEmailVerified(String receiver) {
        Boolean isVerified = verificationStatus.get(receiver);

        if (isVerified == null || !isVerified) {
            throw new MemberException("이메일 인증이 필요합니다.", HttpStatus.BAD_REQUEST);
        }
        return true;
    }

    // 닉네임 중복체크 - 리파지토리 조회 후 중복시 예외 처리
    private void isExistUserNickName(String nickName) {
        if (memberRepository.findByNickName(nickName).isPresent()) {
            throw new MemberException("이미 사용 중인 닉네임입니다.", HttpStatus.BAD_REQUEST);
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