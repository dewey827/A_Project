package com.project.reservation.repository;

import com.project.reservation.entity.DeletedMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface DeletedMemberRepository extends JpaRepository<DeletedMember, Long> {

    void deleteByDeletedAtBefore (LocalDateTime date);
}
