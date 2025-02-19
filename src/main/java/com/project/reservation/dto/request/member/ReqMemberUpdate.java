package com.project.reservation.dto.request.member;

import com.project.reservation.entity.Pet;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ReqMemberUpdate {

    private String password;
    private String passwordCheck;
    private String nickName;
    private String addr;
    private String birth;
    private String phone;
    private List<Pet> pets;

    @Builder
    public ReqMemberUpdate(String password, String passwordCheck, String nickName, String addr, String birth, String phone, List<Pet> pets) {
        this.password = password;
        this.passwordCheck = passwordCheck;
        this.nickName = nickName;
        this.addr = addr;
        this.birth = birth;
        this.phone = phone;
        this.pets = pets;
    }
}
