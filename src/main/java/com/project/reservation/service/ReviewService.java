package com.project.reservation.service;

import com.project.reservation.common.SearchDto;
import com.project.reservation.common.exception.ResourceNotFoundException;
import com.project.reservation.dto.request.review.ReqReviewUpdate;
import com.project.reservation.dto.request.review.ReqReviewWrite;
import com.project.reservation.dto.response.review.ResReviewDetail;
import com.project.reservation.dto.response.review.ResReviewList;
import com.project.reservation.dto.response.review.ResReviewWrite;
import com.project.reservation.entity.Member;
import com.project.reservation.entity.Review;
import com.project.reservation.repository.MemberRepository;
import com.project.reservation.repository.ReviewLikeRepository;
import com.project.reservation.repository.ReviewRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ReviewService {

    private final MemberRepository memberRepository;
    private final ReviewRepository reviewRepository;

    // 모든 리뷰 조회(페이징) - 페이징
    public Page<ResReviewList> getReviews(Pageable pageable) {
        // 페이지네이션된 리뷰 목록 조회
        Page<Review> reviews = reviewRepository.findAllWithMemberAndComments(pageable);
        // 리뷰 목록을 ResReviewList로 변환
        List<ResReviewList> resReviewLists = reviews.getContent().stream()
                .map(ResReviewList::fromEntity)
                .collect(Collectors.toList());
        // 페이지네이션된 결과 반환
        return new PageImpl<>(resReviewLists, pageable, reviews.getTotalElements());
    }

    // 리뷰 등록
    public ResReviewWrite createReview(ReqReviewWrite reqReviewWrite, Member member) {
        // 요청 데이터를 Review 엔티티로 변환
        Review review = ReqReviewWrite.ofEntity(reqReviewWrite);
        // 작성자 회원 정보 조회
        Member writerMember = memberRepository.findByEmail(member.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Member", "Member Email", member.getEmail())
                );
        // 리뷰에 작성자 매핑
        review.setMappingMember(writerMember);
        // 리뷰 저장
        Review saveReview = reviewRepository.save(review);
        // 저장된 리뷰 데이터 반환
        return ResReviewWrite.fromEntity(saveReview, writerMember.getNickName());
    }

    // 리뷰 상세보기
    public ResReviewDetail readReview(Long reviewId) {
        // 리뷰 ID로 리뷰 조회
        Review findReview = reviewRepository.findByIdWithMemberAndComments(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review", "Review Id", String.valueOf(reviewId))
                );
        // 조회수 증가
        findReview.upViews();
        // 조회된 리뷰 데이터 반환
        return ResReviewDetail.fromEntity(findReview);
    }

    // 리뷰 수정
    public ResReviewDetail updateReview(Long reviewId, ReqReviewUpdate reqReviewUpdate) {
        // 리뷰 ID로 기존 리뷰 조회
        Review newReview = reviewRepository.findByIdWithMemberAndComments(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review", "Review Id", String.valueOf(reviewId))
                );
        // 리뷰 내용 수정
        newReview.update(reqReviewUpdate.getTitle(), reqReviewUpdate.getContent());
        // 수정된 리뷰 데이터 반환
        return ResReviewDetail.fromEntity(newReview);
    }

    // 리뷰 삭제
    public void deleteReview(Long reviewId) {
        reviewRepository.deleteById(reviewId);
    }

    // 리뷰 검색
    public Page<ResReviewList> search(SearchDto searchData, Pageable pageable) {
        // 검색 결과를 저장할 변수 초기화
        Page<Review> result = null;
        // 제목이 비어있지 않으면 제목으로 검색
        if (!searchData.getTitle().isEmpty()) {
            result = reviewRepository.findAllTitleContaining(searchData.getTitle(), pageable);
            // 내용이 비어있지 않으면 내용으로 검색
        } else if (!searchData.getContent().isEmpty()) {
            result = reviewRepository.findAllContentContaining(searchData.getContent(), pageable);
            // 작성자 이름이 비어있지 않으면 작성자 이름으로 검색
        } else if (!searchData.getWriterName().isEmpty()) {
            result = reviewRepository.findAllNicknameContaining(searchData.getWriterName(), pageable);
        }
        // 검색 결과를 ResReviewList로 변환
        List<ResReviewList> list = result.getContent().stream()
                .map(ResReviewList::fromEntity)
                .collect(Collectors.toList());
        // 페이지네이션된 결과 반환
        return new PageImpl<>(list, pageable, result.getTotalElements());
    }

    // 리뷰에 포함된 총 좋아요 개수 확인
    public int getLikes(Long reviewId) {
        // 리뷰 ID로 조회, 없으면 예외
        long likeCount = reviewRepository.countLikesByReview(reviewId);
        // 좋아요 개수 계산
        return (int)likeCount;
    }
}