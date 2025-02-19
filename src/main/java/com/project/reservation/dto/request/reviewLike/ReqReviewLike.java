package com.project.reservation.dto.request.reviewLike;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ReqReviewLike {

    private Long memberId;
    private Long reviewId;

    @Builder
    public ReqReviewLike(Long memberId, Long reviewId) {
        this.memberId = memberId;
        this.reviewId = reviewId;

    }
}
