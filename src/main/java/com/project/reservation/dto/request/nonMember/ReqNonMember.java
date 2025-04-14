package com.project.reservation.dto.request.nonMember;

import com.project.reservation.entity.NonMember;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor

public class ReqNonMember {

    private String name;
    private String phoneNum;

    public static NonMember ofEntity(ReqNonMember reqNonMember){
        return NonMember.builder()
                .name(reqNonMember.getName())
                .phoneNum(reqNonMember.getPhoneNum())
                .build();
    }
}