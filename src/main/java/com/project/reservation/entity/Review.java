package com.project.reservation.entity;

import com.project.reservation.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Review extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    @Column(length = 100, nullable = false)
    private String title;

    @Column(length=500, nullable = false)
    private String content;

    @Column(nullable = false, length = 50)
    private String nickName;

    @Column
    private int views = 0;  //0으로 초기화값

    @Column
    private int likes = 0;

    //=================================================================================
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private Member member;

    @OneToMany(mappedBy = "review", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @BatchSize(size = 10)
    private List<Comment> comments = new ArrayList<>();

    //=================================================================================
    @Builder
    public Review(Long id, String title, String content, String nickName, int views, int likes, Member member) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.nickName = nickName;
        this.views = views;
        this.likes = likes;
        this.member = member;
    }

    //=================================================================================
    //Review - Member 양방향 연관관계 매핑
    public void setMember(Member member) {
        this.member = member;
        member.getReviews().add(this);
    }

    //수정
    public void update(String title, String content) {
        this.title = title;
        this.content = content;
    }

    //views 증가
    public void upViews() {
        this.views++;
    }

    //likes 증가
    public void upLikes(){
        this.likes++;
    }

    //likes 감소
    public void downLikes(){
        //0 이하로 내려가지 않게
        if (this.likes > 0){
            this.likes--;
        }
    }
}
