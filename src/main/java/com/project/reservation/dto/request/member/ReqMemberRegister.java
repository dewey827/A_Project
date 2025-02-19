package com.project.reservation.dto.request.member;

import com.project.reservation.entity.Role;
import com.project.reservation.entity.Member;
import com.project.reservation.entity.Pet;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ReqMemberRegister {

    private String name;
    private String email;
//    private boolean emailVerified = false;  // 이메일 인증 여부
//    private String emailVerificationCode;   //이메일 인증 코드
    private String password;
    private String passwordCheck;
    private String nickName;
    private String addr;
    private String birth;
    private String phone;
//    private List<Pet> pets;

    @Builder
    public ReqMemberRegister(
            String name, String email, String emailVerificationCode, String password, String passwordCheck,
            String nickName, String addr, String birth, String phone, List<Pet> pets) {
        this.name = name;
        this.email = email;
//        this.emailVerificationCode = emailVerificationCode;
        this.password = password;
        this.passwordCheck = passwordCheck;
        this.nickName = nickName;
        this.addr = addr;
        this.birth = birth;
        this.phone = phone;
//        this.pets = pets;
    }

    // DTO -> Entity
    public static Member ofEntity(ReqMemberRegister reqMemberRegister) {
        return Member.builder()
                .name(reqMemberRegister.getName())
                .email(reqMemberRegister.getEmail())
                .password(reqMemberRegister.getPassword())
                .nickName(reqMemberRegister.getNickName())
                .addr(reqMemberRegister.getAddr())
                .birth(reqMemberRegister.getBirth())
                .phone(reqMemberRegister.getPhone())
                .roles(Role.USER)
                .build();
    }
}
