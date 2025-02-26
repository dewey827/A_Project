package com.project.reservation.dto.request.member;

import com.project.reservation.dto.request.pet.ReqPet;
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
    private String phone;

    @Builder
    public ReqMemberUpdate(String password, String passwordCheck, String nickName, String addr, String phone) {
        this.password = password;
        this.passwordCheck = passwordCheck;
        this.nickName = nickName;
        this.addr = addr;
        this.phone = phone;
    }
}