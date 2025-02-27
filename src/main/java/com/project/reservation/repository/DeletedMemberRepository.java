package com.project.reservation.repository;

import com.project.reservation.entity.DeletedMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface DeletedMemberRepository extends JpaRepository<DeletedMember, Long> {
    Optional<DeletedMember> findByOriginalId(Long originalId);
    void deleteByDeletedAtBefore(LocalDateTime date);
}
