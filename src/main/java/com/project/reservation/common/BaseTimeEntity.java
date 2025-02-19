package com.project.reservation.common;

import jakarta.persistence.*;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@MappedSuperclass   //다른 엔티티 클래스들이 상속할 수 있게
@EntityListeners(AuditingEntityListener.class)  //엔티티가 생성되거나 업데이트될 때 자동으로 시간설정
public class BaseTimeEntity {

    @CreatedDate
    @Column(name = "create_date", updatable = false)
    private String createdDate;

    @LastModifiedDate
    @Column(name = "modified_date")
    private String modifiedDate;

    //엔티티가 처음 저장되기 전에. 현재 시간을 생성 시간과 수정 시간으로 설정.
    @PrePersist
    public void onPrePersist(){
        this.createdDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"));
        this.modifiedDate = this.createdDate;
    }
    //엔티티가 수정되기 전에. 현재 시간을 수정 시간으로 설정.
    @PreUpdate
    public void onPreUpdate(){
        this.modifiedDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"));
    }
}
