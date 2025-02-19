package com.project.reservation.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "review_likes", uniqueConstraints = { @UniqueConstraint(columnNames = {"review_id", "member_id"})})
public class ReviewLike {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    //============================================================
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Review review;

    //============================================================
    @Builder
    public ReviewLike(Long id, Member member, Review review) {
        this.id = id;
        this.member = member;
        this.review = review;
    }
}

