package com.project.reservation.dto.response.review;

import com.project.reservation.dto.response.comment.ResComment;
import com.project.reservation.entity.Review;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
public class ResReviewDetail {

    private Long reviewId;
    private String title;
    private String nickName;
    private String content;
    private LocalDateTime createdDate;
    private int views;
    private int likes;  // 좋아요 + 댓글은 dto 에서 제외

    @Builder
    public ResReviewDetail(Long reviewId, String title, String nickName, String content, LocalDateTime createdDate, int views, int likes) {
        this.reviewId = reviewId;
        this.title = title;
        this.nickName = nickName;
        this.content = content;
        this.createdDate = createdDate;
        this.views = views;
        this.likes = likes;
    }

    // Entity -> DTO
    public static ResReviewDetail fromEntity(Review review){
        return ResReviewDetail.builder()
                .reviewId(review.getId())
                .title(review.getTitle())
                .nickName(review.getMember().getNickName())
                .content(review.getContent())
                .createdDate(review.getCreatedDate())
                .views(review.getViews())
                .likes(review.getLikes())
                .build();
    }
}
