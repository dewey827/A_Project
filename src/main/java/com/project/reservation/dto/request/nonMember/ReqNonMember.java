package com.project.reservation.dto.request.nonMember;

import com.project.reservation.entity.NonMember;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor

public class ReqNonMember {

    private String name;
    private String phoneNum;

    @Builder
    public ReqNonMember(String name, String phoneNum){
        this.name = name;
        this.phoneNum = phoneNum;
    }

    public static NonMember ofEntity(ReqNonMember reqNonMember){
        return NonMember.builder()
                .name(reqNonMember.getName())
                .phoneNum(reqNonMember.getPhoneNum())
                .build();
    }
}