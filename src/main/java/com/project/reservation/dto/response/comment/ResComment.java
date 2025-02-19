package com.project.reservation.dto.response.comment;

import com.project.reservation.entity.Comment;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class ResComment {

    private Long commentId;
    private String content;
    private String nickName;
    private LocalDateTime createdDate;

    @Builder
    public ResComment(Long commentId, String content, String nickName, LocalDateTime createdDate) {
        this.commentId = commentId;
        this.content = content;
        this.nickName = nickName;
        this.createdDate = createdDate;
    }

    // Entity -> DTO
    public static ResComment fromEntity(Comment comment) {
        return ResComment.builder()
                .commentId(comment.getId())
                .content(comment.getContent())
                .nickName(comment.getMember().getNickName())
                .createdDate(comment.getCreatedDate())
                .build();
    }
}




