package com.project.reservation.dto.request.comment;

import com.project.reservation.entity.Comment;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ReqComment {

    private String content;

    @Builder
    public ReqComment(String content) {
        this.content = content;
    }

    // DTO -> Entity
    public static Comment ofEntity(ReqComment reqComment){
        return Comment.builder()
                .content(reqComment.getContent())
                .build();
    }
}
