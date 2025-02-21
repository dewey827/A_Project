package com.project.reservation.dto.request.member;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ReqMemberFindId {
    private String name;
    private String phone;
    private String email;

    @Builder
    public ReqMemberFindId(String name, String phone, String email){
        this.name = name;
        this.phone = phone;
        this.email = email;
    }


}
