package com.project.reservation.repository;

import com.project.reservation.entity.Member;
import com.project.reservation.entity.Review;
import com.project.reservation.entity.ReviewLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReviewLikeRepository extends JpaRepository<ReviewLike, Long> {

    //특정 리뷰와 특정 멤버의 좋아요 정보 조회
    Optional<ReviewLike> findByReviewAndMember(Review review, Member member);

    //특정 리뷰와 특정 멤버 간 좋아요 존재 여부 확인
    boolean existsByReviewAndMember(Review review, Member member);
}