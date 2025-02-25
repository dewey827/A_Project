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

    @Builder
    public ReqMemberFindId(String name, String phone){
        this.name = name;
        this.phone = phone;
    }
}