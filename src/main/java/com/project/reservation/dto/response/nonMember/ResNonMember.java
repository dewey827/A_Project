package com.project.reservation.dto.response.nonMember;

import com.project.reservation.entity.NonMember;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor

public class ResNonMember {
    private Long id;
    private String name;
    private String phoneNum;

    @Builder
    public ResNonMember(Long id,String name, String phoneNum){
        this.id = id;
        this.name = name;
        this.phoneNum = phoneNum;
    }

    public static ResNonMember fromEntity(NonMember nonMember){
        return ResNonMember.builder()
                .id(nonMember.getId())
                .name(nonMember.getName())
                .phoneNum(nonMember.getPhoneNum())
                .build();
    }
}