package com.project.reservation.dto.request.review;

import com.project.reservation.entity.Review;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor

public class ReqReviewWrite {

    private String title;
    private String content;


    @Builder
    public ReqReviewWrite(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public static Review ofEntity(ReqReviewWrite reqReviewWrite, String nickName){
        return Review.builder()
                .title(reqReviewWrite.title)
                .content(reqReviewWrite.content)
                .nickName(nickName)
                .build();
    }
}