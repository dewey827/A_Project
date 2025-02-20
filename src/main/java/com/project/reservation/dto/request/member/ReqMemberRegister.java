package com.project.reservation.dto.request.member;

import com.project.reservation.dto.request.pet.ReqPet;
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
    private String password;
    private String passwordCheck;
    private String nickName;
    private String addr;
    private String birth;
    private String phone;
    private List<ReqPet> pets;

    @Builder
    public ReqMemberRegister(
            String name, String email,String password, String passwordCheck,
            String nickName, String addr, String birth, String phone, List<ReqPet> pets) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.passwordCheck = passwordCheck;
        this.nickName = nickName;
        this.addr = addr;
        this.birth = birth;
        this.phone = phone;
        this.pets = pets;
    }

    // DTO -> Entity
    public static Member ofEntity(ReqMemberRegister reqMemberRegister) {

        Member member = Member.builder()
                .name(reqMemberRegister.getName())
                .email(reqMemberRegister.getEmail())
                .password(reqMemberRegister.getPassword())
                .nickName(reqMemberRegister.getNickName())
                .addr(reqMemberRegister.getAddr())
                .birth(reqMemberRegister.getBirth())
                .phone(reqMemberRegister.getPhone())
                .roles(Role.USER)
                .build();
        // 펫 리스트
        for (ReqPet reqPet : reqMemberRegister.getPets()) {
            Pet pet = Pet.builder()
                    .name(reqPet.getName())
                    .breed(reqPet.getBreed())
                    .age(reqPet.getAge())
                    .weight(reqPet.getWeight())
                    .member(member)
                    .build();

            member.addPet(pet);
        }
        return member;
    }

}
