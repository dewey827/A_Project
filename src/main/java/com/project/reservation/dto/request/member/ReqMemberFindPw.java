package com.project.reservation.dto.request.member;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ReqMemberFindPw {
    private String email;
    private String code;
    private String newPassword;
    private String newPasswordCheck;

    @Builder
    public ReqMemberFindPw(String email, String code, String newPassword, String newPasswordCheck){
        this.email = email;
        this.code = code;
        this.newPassword = newPassword;
        this.newPasswordCheck = newPasswordCheck;
    }
}
