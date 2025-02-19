package com.project.reservation.dto.request.review;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor

public class ReqReviewUpdate {

    private String title;
    private String content;

    @Builder
    public ReqReviewUpdate(String title, String content) {
        this.title = title;
        this.content = content;
    }
}
