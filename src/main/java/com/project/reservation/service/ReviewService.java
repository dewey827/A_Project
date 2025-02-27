package com.project.reservation.service;

import com.project.reservation.common.SearchDto;
import com.project.reservation.common.exception.MemberException;
import com.project.reservation.common.exception.ResourceNotFoundException;
import com.project.reservation.common.exception.ReviewException;
import com.project.reservation.dto.request.review.ReqReviewUpdate;
import com.project.reservation.dto.request.review.ReqReviewWrite;
import com.project.reservation.dto.response.review.ResReviewDetail;
import com.project.reservation.dto.response.review.ResReviewList;
import com.project.reservation.entity.Member;
import com.project.reservation.entity.Review;
import com.project.reservation.repository.MemberRepository;
import com.project.reservation.repository.ReviewLikeRepository;
import com.project.reservation.repository.ReviewRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
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

    // 리뷰 검색
    public Page<ResReviewList> search(SearchDto searchData, Pageable pageable) {
        // 검색 결과를 저장할 변수 초기화
        Page<Review> result = null;

        if (searchData.getTitle() != null && !searchData.getTitle().isEmpty()) {
            result = reviewRepository.findAllTitleContaining(searchData.getTitle(), pageable);
        } else if (searchData.getContent() != null && !searchData.getContent().isEmpty()) {
            result = reviewRepository.findAllContentContaining(searchData.getContent(), pageable);
        } else if (searchData.getWriterName() != null && !searchData.getWriterName().isEmpty()) {
            result = reviewRepository.findAllNicknameContaining(searchData.getWriterName(), pageable);
        }

        // 검색 결과를 ResReviewList로 변환
        List<ResReviewList> list = result.getContent().stream()
                .map(ResReviewList::fromEntity)
                .collect(Collectors.toList());
        // 페이지네이션된 결과 반환
        return new PageImpl<>(list, pageable, result.getTotalElements());
    }

    // 리뷰 등록
    public ResReviewDetail createReview(ReqReviewWrite reqReviewWrite, Member member) {
        // 요청 데이터를 Review 엔티티로 변환
        Review review = ReqReviewWrite.ofEntity(reqReviewWrite);
        // 작성자 회원 정보 조회
        Member writerMember = memberRepository.findById(member.getId()).orElseThrow(
                () -> new ResourceNotFoundException("Member", "Member Email", member.getEmail())
        );
        // 리뷰에 작성자 매핑
        review.setMember(writerMember);

        // 리뷰 저장
        Review saveReview = reviewRepository.save(review);
        // 저장된 리뷰 데이터 반환
        return ResReviewDetail.fromEntity(saveReview);
    }

    // 리뷰 상세보기
    public ResReviewDetail readReview(@Param("reviewId") Long reviewId) {
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
    public ResReviewDetail updateReview(@Param("reviewId") Long reviewId, ReqReviewUpdate reqReviewUpdate, Member currentMember) {
        // 리뷰 ID로 기존 리뷰 조회
        Review review = reviewRepository.findByIdWithMemberAndComments(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review", "Review Id", String.valueOf(reviewId))
                );

        // 현재 로그인한 사용자와 리뷰 작성자 비교
        if (!review.getMember().getId().equals(currentMember.getId())) {
            throw new ReviewException("후기 작성자만 수정할 수 있습니다.", HttpStatus.BAD_REQUEST);
        }
        // 리뷰 내용 수정
        review.update(reqReviewUpdate.getTitle(), reqReviewUpdate.getContent());
        // 수정된 리뷰 저장
        Review savedReview = reviewRepository.save(review);
        // 수정된 리뷰 데이터 반환
        return ResReviewDetail.fromEntity(savedReview);
    }

    // 리뷰 삭제
    @Transactional
    public void deleteReview(Long reviewId, Member currentMember) {

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review", "Review Id", String.valueOf(reviewId)));

        // 현재 로그인한 사용자와 리뷰 작성자 비교
        if (!review.getMember().getId().equals(currentMember.getId())) {
            throw new ReviewException("후기 작성자만 삭제할 수 있습니다.", HttpStatus.BAD_REQUEST);
        }

        reviewRepository.deleteById(reviewId);
    }

//    // 리뷰에 포함된 총 좋아요 개수 확인 - 불필요
//    public int getLikes(Long reviewId) {
//        // 리뷰 ID로 조회
//        int likeCount = reviewRepository.countLikesByReview(reviewId);
//        // 좋아요 개수 계산
//        return (int)likeCount;
//    }
}