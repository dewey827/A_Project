package com.project.reservation.controller;

import com.project.reservation.common.SearchDto;
import com.project.reservation.dto.request.review.ReqReviewUpdate;
import com.project.reservation.dto.request.review.ReqReviewWrite;
import com.project.reservation.dto.response.review.ResReviewDetail;
import com.project.reservation.dto.response.review.ResReviewList;
import com.project.reservation.entity.Member;
import com.project.reservation.service.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.Param;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/review")
public class ReviewController {

    private final ReviewService reviewService;

    // 리뷰 페이징 목록
    @GetMapping("/list")
    public ResponseEntity<Page<ResReviewList>> reviewList(
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<ResReviewList> listDTO = reviewService.getReviews(pageable);
        return ResponseEntity.status(HttpStatus.OK).body(listDTO);
    }

    // 리뷰 검색
    @GetMapping("/search")
    public ResponseEntity<Page<ResReviewList>> search(
            @PageableDefault(size = 5, sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "content", required = false) String content,
            @RequestParam(value = "writerName", required = false) String writerName) {
        SearchDto searchDto = SearchDto.searchData(title, content, writerName);
        Page<ResReviewList> searchReview = reviewService.search(searchDto, pageable);
        return  ResponseEntity.status(HttpStatus.OK).body(searchReview);
    }

    // 리뷰 상세조회
    @GetMapping("/{reviewId}")
    public ResponseEntity<ResReviewDetail> detail(
            @PathVariable("reviewId")
            @Param("reviewId") Long reviewId
    ) {
        ResReviewDetail getDetail = reviewService.readReview(reviewId);
        return ResponseEntity.status(HttpStatus.OK).body(getDetail);
    }

    // 리뷰 작성
    @PostMapping("/write")
    public ResponseEntity<ResReviewDetail> write(
            @RequestBody ReqReviewWrite reqReviewWrite,
            @AuthenticationPrincipal Member member
    ) {
        ResReviewDetail saveReviewDTO = reviewService.createReview(reqReviewWrite, member);
        return ResponseEntity.status(HttpStatus.CREATED).body(saveReviewDTO);
    }

    // 리뷰 수정
    @PatchMapping("/{reviewId}/update")
    public ResponseEntity<ResReviewDetail> update(
            @PathVariable("reviewId") Long reviewId,
            @RequestBody ReqReviewUpdate reqReviewUpdate,
            @AuthenticationPrincipal Member member) {
        ResReviewDetail resReviewDetail = reviewService.updateReview(reviewId, reqReviewUpdate, member);
        return ResponseEntity.status(HttpStatus.OK).body(resReviewDetail);
    }
    
    // 리뷰 삭제
    @DeleteMapping("/{reviewId}/delete")
    public ResponseEntity<Long> delete(
            @PathVariable("reviewId") Long reviewId,
            @AuthenticationPrincipal Member member) {
        reviewService.deleteReview(reviewId, member);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

//    // GET - 리뷰에 포함된 총 좋아요 개수 반환 API - 불필요
//    @GetMapping("/{reviewId}/likes")
//    public ResponseEntity<Integer> getLikes(
//            @PathVariable("reviewId")
//            @Param("reviewId") Long reviewId) {
//        int likes = reviewService.getLikes(reviewId);
//        return ResponseEntity.status(HttpStatus.OK).body(likes);
//    }
}
