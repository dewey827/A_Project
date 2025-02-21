package com.project.reservation.dto.response.review;

import com.project.reservation.entity.Review;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class ResReviewWrite {

    private Long reviewId;
    private String title;
    private String nickName;
    private String content;
    private LocalDateTime createdDate;

    @Builder
    public ResReviewWrite(Long reviewId, String title, String nickName, String content, LocalDateTime createdDate) {
        this.reviewId = reviewId;
        this.title = title;
        this.nickName = nickName;
        this.content = content;
        this.createdDate = createdDate;
    }

    // Entity -> DTO
    // nickName 은 그냥 (nickName)
    public static ResReviewWrite fromEntity(Review review, String nickName){
        return ResReviewWrite.builder()
                .reviewId(review.getId())
                .title(review.getTitle())
                .nickName(nickName)
                .content(review.getContent())
                .createdDate(review.getCreatedDate())
                .build();
    }
}
