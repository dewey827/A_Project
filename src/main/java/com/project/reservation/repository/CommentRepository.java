package com.project.reservation.repository;

import com.project.reservation.entity.Comment;
import com.project.reservation.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    void deleteByMember(Member member);

    @Query(value = "SELECT c FROM Comment c JOIN FETCH c.member m JOIN FETCH c.review r WHERE r.id = :reviewId")
    Page<Comment> findAllWithMemberAndReview(Pageable pageable, @Param("reviewId") Long reviewId);

    @Query(value = "SELECT c FROM Comment c JOIN FETCH c.member m JOIN FETCH c.review r WHERE c.id = :commentId")
    Optional<Comment> findByIdWithMemberAndReview(@Param("commentId") Long commentId);
}
