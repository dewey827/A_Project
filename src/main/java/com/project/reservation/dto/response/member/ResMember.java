package com.project.reservation.dto.response.member;

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
public class ResMember {

    private String name;
    private String email;
    private String nickName;
    private String addr;
    private String birth;
    private String phone;
    private List<Pet> pets;

    @Builder
    public ResMember(String name, String email, String password, String nickName, String addr, String birth, String phone, List<Pet> pets) {
        this.name = name;
        this.email = email;
        this.nickName = nickName;
        this.addr = addr;
        this.birth = birth;
        this.phone = phone;
        this.pets = pets;
    }

    // Entity -> DTO
    public static ResMember fromEntity(Member member) {
        return ResMember.builder()
                .name(member.getName())
                .email(member.getEmail())
                .password(member.getPassword())
                .nickName(member.getNickName())
                .addr(member.getAddr())
                .birth(member.getBirth())
                .phone(member.getPhone())
                .pets(member.getPets())
                .build();
    }
}
