package com.project.reservation.service;

import com.project.reservation.dto.request.reviewLike.ReqReviewLike;
import com.project.reservation.dto.response.review.ResReviewDetail;
import com.project.reservation.entity.Member;
import com.project.reservation.entity.Review;
import com.project.reservation.entity.ReviewLike;
import com.project.reservation.repository.MemberRepository;
import com.project.reservation.repository.ReviewLikeRepository;
import com.project.reservation.repository.ReviewRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReviewLikeService {
    private final ReviewLikeRepository reviewLikeRepository;
    private final ReviewRepository reviewRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public String toggleLike(ReqReviewLike reqReviewLike) {
        //리뷰 ID로 조회, 없으면 예외
        Review review = reviewRepository.findById(reqReviewLike.getReviewId())
                .orElseThrow(() -> new RuntimeException("Review not found"));

        //멤버 ID로 조회, 없으면 예외
        Member member = memberRepository.findById(reqReviewLike.getMemberId())
                .orElseThrow(() -> new RuntimeException("Member not found"));

        //기존 좋아요 여부 확인
        Optional<ReviewLike> existingLike = reviewLikeRepository.findByReviewAndMember(review, member);

        if (existingLike.isPresent()) {
            //좋아요가 이미 존재하면 삭제
            reviewLikeRepository.delete(existingLike.get());
            review.downLikes(); // 리뷰 좋아요 수 감소
            return "좋아요를 취소했습니다.";
        } else {
            // 좋아요가 없으면 추가
            ReviewLike newLike = ReviewLike.builder()
                    .review(review)
                    .member(member)
                    .build();
            reviewLikeRepository.save(newLike); // 좋아요 저장
            review.upLikes(); // 리뷰 좋아요 수 증가
            return "좋아요를 눌렀습니다.";
        }
    }

    //사용자가 특정 리뷰에 이미 좋아요를 눌렀는지 여부 확인
    public boolean hasLiked(Long reviewId, Long memberId) {
        //리뷰 ID로 조회, 없으면 예외
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));
        //멤버 ID로 조회, 없으면 예외
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found"));

        boolean hasLiked = reviewLikeRepository.existsByReviewAndMember(review, member);

//        return ResReviewDetail.fromEntity(review, hasLiked);
        //특정 사용자가 특정 리뷰를 좋아요했는지 여부 반환
        return reviewLikeRepository.existsByReviewAndMember(review, member);
    }

    //총 좋아요 수
    public int countLike(Long reviewId) {
        //리뷰 ID로 조회, 없으면 예외
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));
        return review.getLikes();
    }
}