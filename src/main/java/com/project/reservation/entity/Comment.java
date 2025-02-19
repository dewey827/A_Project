package com.project.reservation.entity;

import com.project.reservation.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Comment extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    private String content;

    //============================================================
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private Review review;

    //============================================================
    @Builder
    public Comment(Long id, String content, Member member, Review review) {
        this.id = id;
        this.content = content;
        this.member = member;
        this.review = review;
    }

    //============================================================
    //Review - Comment 연관관계 편의 메소드
    public void setReview(Review review) {
        this.review = review;
        review.getComments().add(this); // Review 엔티티에도 Comment 추가
    }

    //Member - Comment 연관관계 편의 메소드
    public void setMember(Member member) {
        this.member = member;
        member.getComments().add(this); // Member 엔티티에도 Comment 추가
    }

    //수정
    public void update(String content) {
        this.content = content;
    }
}
