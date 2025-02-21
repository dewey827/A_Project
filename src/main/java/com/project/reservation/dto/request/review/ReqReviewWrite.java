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
    private String nickName;

    @Builder
    public ReqReviewWrite(String title, String content, String nickName) {
        this.title = title;
        this.content = content;
        this.nickName = nickName;
    }

    public static Review ofEntity(ReqReviewWrite reqReviewWrite){
        return Review.builder()
                .title(reqReviewWrite.title)
                .content(reqReviewWrite.content)
                .nickName(reqReviewWrite.nickName)
                .build();
    }
}